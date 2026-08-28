package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


//added comment

//Ben is adding a comment

//bode is adding a comment
@TeleOp(name="Field Centric TeleOp Tutorial")
public class FieldCentricDriveTutorial extends LinearOpMode {

    private GoBildaPinpointDriver odo;

    private DcMotor frontLeftDrive;
    private DcMotor backLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backRightDrive;
    
    private DcMotor elevator;
    
     // TODO: Adjust this value to match your motor encoder counts per inch of travel!
    //final double COUNTS_PER_INCH = 453.7; // Example for a GOBUG/REV/AndyMark motor with standard ticks
    final double COUNTS_PER_INCH = 362.9; // Example for a GOBUG/REV/AndyMark motor with standard ticks

    //need to set the current elevator position to 0
    int elevatorCurrentPosition = 0;
    
    @Override
    public void runOpMode() {
        
        elevator = hardwareMap.get(DcMotor.class, "elevator");
        
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        
        // Set directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD); 
        
        // Optional: Zero power behavior keeps the lift from falling under gravity
        elevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialize using the absolute package path
        odo = hardwareMap.get(com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.class, "odo");

        odo.setOffsets(-114.3, -171.45, DistanceUnit.MM);
    
        // Set odometry pod resolution for the swingarm (spring) pod
        odo.setEncoderResolution(com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        // Set directions
        odo.setEncoderDirections(com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.EncoderDirection.FORWARD, 
                                 com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odo.resetPosAndIMU();
        Pose2D startingPosition = new Pose2D(DistanceUnit.MM, -923.925, 1601.47, AngleUnit.RADIANS, 0);
        odo.setPosition(startingPosition);
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");

            // Drive Train Logic
            double forward = -gamepad1.left_stick_y;  //Remember, y is reversed
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;  //Rotation
            
            Pose2D pos = odo.getPosition();
            double heading = pos.getHeading(AngleUnit.RADIANS);
            
            double cosAngle = Math.cos((Math.PI / 2) - heading);
            double sinAngle = Math.sin((Math.PI / 2) - heading);
            
            double globalStrafe = -forward * sinAngle + strafe * cosAngle;
            double globalForward = forward * cosAngle + strafe * sinAngle;
            
            double frontLeftPower =  globalForward + globalStrafe + rotate;
            double frontRightPower =  globalForward - globalStrafe - rotate;
            double backLeftPower =  globalForward - globalStrafe + rotate;
            double backRightPower =  globalForward + globalStrafe - rotate;
            
            frontLeftDrive.setPower(frontLeftPower);
            backLeftDrive.setPower(backLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backRightDrive.setPower(backRightPower);
            
            // Get the current position and convert to inches
            elevatorCurrentPosition = elevator.getCurrentPosition();
            double currentInches = elevatorCurrentPosition / COUNTS_PER_INCH;
             
            if (gamepad1.y) {
                // Manual UP: Only move if we are below the 20-inch safety limit
                if (currentInches > -20.0) {
                    elevator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    elevator.setPower(-0.8);
                } else {
                    elevator.setPower(0.0); // Safety stop at 20 inches
                }
            } 
            else if (gamepad1.a) {
                // Manual DOWN: Only move if we are above the bottom safety limit
                if (currentInches < 3.5) {
                    elevator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    elevator.setPower(0.6);
                } else {
                    elevator.setPower(0.0); // Safety stop at the bottom
                }
            } 
            else if (gamepad1.x) {
                // Automatic Target: Use RUN_TO_POSITION to hit exactly 10 inches
                int targetTicks = (int)(-10.0 * COUNTS_PER_INCH);
                elevator.setTargetPosition(targetTicks);
                elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                elevator.setPower(0.8); // This acts as the maximum speed limit to get there
            } 
            else {
                // If no buttons are pressed, stop the motor
                // Note: If X was just pressed, RUN_TO_POSITION will keep holding its spot 
                // unless we explicitly tell it to stop when the button is released.
                if (elevator.getMode() != DcMotor.RunMode.RUN_TO_POSITION || !elevator.isBusy()) {
                    elevator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    elevator.setPower(0.0);
                }
            }


            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Right Power", backRightPower);
            telemetry.addData("Elevator Ticks", elevatorCurrentPosition);
            telemetry.addData("Elevator Inches", currentInches);
            telemetry.addData("Elevator Power", elevator.getPower());
            telemetry.update();
        }
    }
}
