package AS3Solution;

import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import AS3Solution.Frames.FrameKeys;

public class SotaInverseK {

    private static double NUMERICAL_DELTA_rad = 1e-10;
    private static double DISTANCE_THRESH = 1e-3; // 1mm
    private static int MAX_TRIES = 15; // loop 15 times

    public enum JType { // We separate the jacobians into origin and rotation components to simplify the
                        // problem
        O, // origin
        R; // rotation / orientation

        public static final int OUT_DIM = 3; // each has 3 outputs
    }

    public TreeMap<FrameKeys, RealMatrix>[] J;
    public TreeMap<FrameKeys, RealMatrix>[] Jinv;

    @SuppressWarnings("unchecked")
    SotaInverseK(RealVector currentAngles, FrameKeys frameType) {
        J = new TreeMap[JType.values().length]; // length 2
        Jinv = new TreeMap[JType.values().length]; // length 2
        for (int i = 0; i < JType.values().length; i++) { // initialize each entry as a TreeMap
            J[i] = new TreeMap<FrameKeys, RealMatrix>();
            Jinv[i] = new TreeMap<FrameKeys, RealMatrix>();
        }
        makeJacobian(currentAngles, frameType); // compute Jacobian matrix
    }

    // Makes both the jacobian and inverse from the current configuration for the
    // given frame type. Creates both JTypes.
    private void makeJacobian(RealVector currentAngles, FrameKeys frameType) {
        // TODO
        // Get frame's corresponding motor angles (L_HAND, R_HAND, or HEAD)
        int[] frameIndices = frameType.motorindices; // get index of motor joints in the frame

        RealVector frameAngles = MatrixUtils.createRealVector(new double[frameIndices.length]);
        for (int i = 0; i < frameAngles.getDimension(); i++) {
            frameAngles.setEntry(i, currentAngles.getEntry(frameIndices[i]));
        }
        // System.out.println("frameAngles: "+frameAngles);
        System.out.println();

        int numMotors = frameIndices.length; // number of motors (3 for left & right hand, 4 for head)

        // Create origin and rotation matrices - 3x3 or 3x4
        RealMatrix jacobianO = MatrixUtils.createRealMatrix(JType.OUT_DIM, numMotors); 
        RealMatrix jacobianR = MatrixUtils.createRealMatrix(JType.OUT_DIM, numMotors); 

        for (int i = 0; i < numMotors; i++) {
            // Solve FK for current angles -> f(theta)
            SotaForwardK currentFK = new SotaForwardK(currentAngles); // solve FK for the whole robot
            RealVector currentPose = MatrixHelp.getTrans(currentFK.frames.get(frameType)).getSubVector(0, 3); // but get the current pose of the required frame

            // Solve FK for pertubed angles -> f(theta + delta_theta)
            RealVector pertubedAngles = currentAngles.copy(); // copy motor angles of that frame, reset every iteration
            pertubedAngles.setEntry(frameIndices[i], pertubedAngles.getEntry(frameIndices[i]) + NUMERICAL_DELTA_rad); // theta + delta_theta of the correct joint

            SotaForwardK perturbedFK = new SotaForwardK(pertubedAngles);
            RealVector perturbedPose = MatrixHelp.getTrans(perturbedFK.frames.get(frameType)).getSubVector(0, 3); // get the pertubed pose of the required frame

            // derivate = (current - delta) / SMALL_DETA
            RealVector derivativesO = (perturbedPose.subtract(currentPose)).mapDivide(NUMERICAL_DELTA_rad);
            // System.out.println("derivativesO:" + derivativesO);
            jacobianO.setColumnVector(i, derivativesO); // set the column of the jacobian

            // Build rotation Jacobian
            RealVector currentRotation = MatrixUtils.createRealVector(MatrixHelp.getYPR(currentFK.frames.get(frameType)));
            RealVector perturbedRotation = MatrixUtils.createRealVector(MatrixHelp.getYPR(perturbedFK.frames.get(frameType)));

            RealVector derivativesR = (perturbedRotation.subtract(currentRotation)).mapDivide(NUMERICAL_DELTA_rad);
            //System.out.println("derivativesR:" + derivativesR);
            //System.out.println();
            jacobianR.setColumnVector(i, derivativesR);
        }
        J[JType.O.ordinal()].put(frameType, jacobianO); // origin jacobian
        J[JType.R.ordinal()].put(frameType, jacobianR); // rotation jacobian

        // Compute inverse Jacobian using pseudo-inverse
        Jinv[JType.O.ordinal()].put(frameType, MatrixHelp.pseudoInverse(jacobianO)); // origin jacobian inverse
        Jinv[JType.R.ordinal()].put(frameType, MatrixHelp.pseudoInverse(jacobianR)); // rotation jacobian inverse
    }

    // calculates the target absolute pose from the current pose, plus the given delta using FK before calling solve.
    static public RealVector solveDelta(FrameKeys frameType, JType jtype, RealVector deltaEndPose, RealVector curMotorAngles) {
        // TODO if needed
        return solve(frameType, jtype, null, curMotorAngles);
    }

    // solves for the target pose on the given frame and type, starting at the current angle configuration.
    static public RealVector solve(FrameKeys frameType, JType jtype, RealVector targetPose, RealVector curMotorAngles) {
        System.out.println("Solving for " + frameType);
        // a copy of current angles, use this for later FK & IK calculation, not curMotorAngles
        RealVector solution = curMotorAngles.copy(); 
        int tries = 0;
        int[] frameIndices = frameType.motorindices; // get index of motor joints in the frame

        SotaForwardK FK = new SotaForwardK(curMotorAngles);
        RealVector currentPose = MatrixHelp.getTrans(FK.frames.get(frameType)).getSubVector(0, 3);
        RealVector error = targetPose.subtract(currentPose); // first error

        while (error.getNorm() > DISTANCE_THRESH && tries < MAX_TRIES) {
            // Use FK to calculate the pose at the current "solution"
            FK = new SotaForwardK(curMotorAngles);
            MatrixHelp.printVector("Calculating at solution", solution);
            currentPose = MatrixHelp.getTrans(FK.frames.get(frameType)).getSubVector(0, 3);

            // Instantiate an IK object to solve the Jacobian at the current solution.
            SotaInverseK IK = new SotaInverseK(curMotorAngles, frameType);

            // Calculate error from the current pose and the target pose, as a vector.
            error = targetPose.subtract(currentPose);
            System.out.println("error norm: " + error.getNorm());

            // Use the Jacobian inverse to translate the current error vector into suggestd deltas
            RealMatrix jacobianInverse = IK.Jinv[jtype.ordinal()].get(frameType);
            RealVector deltaTheta = jacobianInverse.operate(error);
            MatrixHelp.printVector("deltaTheta", deltaTheta);
            
            // save a reference of new solution so we don't change solution on accident
            // Update solution using these deltas
            for (int i = 0; i < deltaTheta.getDimension(); i++) {
                curMotorAngles.setEntry(frameIndices[i], curMotorAngles.getEntry(frameIndices[i]) + deltaTheta.getEntry(i));
            }
            MatrixHelp.printVector("Theta_{i+1}", curMotorAngles);

            // FK at theta_{i+1}
            FK = new SotaForwardK(curMotorAngles);
            RealVector newPose = MatrixHelp.getTrans(FK.frames.get(frameType)).getSubVector(0, 3);
            RealVector newError = targetPose.subtract(newPose);

            System.out.println("newError norm: " + newError.getNorm());
            boolean isNewErrorLess = newError.getNorm() < error.getNorm();
            System.out.println("newError < error? : " + isNewErrorLess);
            
            if (newError.getNorm() < error.getNorm()) {
                error = newError;
                solution = curMotorAngles.copy();
                MatrixHelp.printVector("Solution updated", solution);
            }

            tries++;
        }
        MatrixHelp.printVector("Final solution: ", solution);
        return solution;
    }
}