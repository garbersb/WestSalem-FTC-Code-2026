
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

//making change for demonstration

@Autonomous(name="Autonomous Ben: Forward 2 Feet", group="Autonomous")
public class AutonomousBen extends LinearOpMode {

    // Declare your 4 drive motors
    private DcMotor leftFront  = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack   = null;
    private DcMotor rightBack  = null;

    // --- goBilda 5203 Series Motor Configurations ---
    // Adjust TICKS_PER_REV based on your specific motor RPM (e.g., 312 RPM = 537.7, 435 RPM = 384.5)
    static final double TICKS_PER_REV    = 537.7;    // Encoder ticks for goBilda 312 RPM Yellow Jacket
    static final double GEAR_REDUCTION   = 1.0;      // 1.0 if direct drive (no external gears/chains)
    static final double WHEEL_DIAMETER_INCHES = 3.77953; // goBilda 96mm Mecanum wheels in inches

    // Formula to calculate ticks per inch traveled
    static final double COUNTS_PER_INCH  = (TICKS_PER_REV * GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {

        // 1. Initialize hardware (Ensure these names match your Driver Station Configuration)
        leftFront  = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFront = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back_drive");

        // 2. Reverse left-side motors so positive power moves the robot forward
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // 3. Reset encoders and set motors to use them
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized. Ready to start.");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if (opModeIsActive()) {
            // Step 4: Drive Forward 24 inches (2 feet) at 40% speed
            encoderDrive(0.4, 24.0);
        }
    }

    /**
     * Method to perform a straight-line encoder-driven move.
     * @param speed Target speed (0.0 to 1.0)
     * @param inches Distance to move in inches (Positive = Forward, Negative = Backward)
     */
    public void encoderDrive(double speed, double inches) {
        int newLeftFrontTarget;
        int newRightFrontTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        if (opModeIsActive()) {
            // Determine new target position, and calculate target ticks
            int moveTicks = (int)(inches * COUNTS_PER_INCH);

            newLeftFrontTarget  = leftFront.getCurrentPosition()  + moveTicks;
            newRightFrontTarget = rightFront.getCurrentPosition() + moveTicks;
            newLeftBackTarget   = leftBack.getCurrentPosition()   + moveTicks;
            newRightBackTarget  = rightBack.getCurrentPosition()  + moveTicks;

            // Set target positions to the motors
            leftFront.setTargetPosition(newLeftFrontTarget);
            rightFront.setTargetPosition(newRightFrontTarget);
            leftBack.setTargetPosition(newLeftBackTarget);
            rightBack.setTargetPosition(newRightBackTarget);

            // Turn on RUN_TO_POSITION mode
            setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Start motion
            leftFront.setPower(Math.abs(speed));
            rightFront.setPower(Math.abs(speed));
            leftBack.setPower(Math.abs(speed));
            rightBack.setPower(Math.abs(speed));

            // Keep looping while we are still active and all motors are running to the target
            while (opModeIsActive() &&
                    (leftFront.isBusy() && rightFront.isBusy() && leftBack.isBusy() && rightBack.isBusy())) {

                // Display progress to the Driver Station
                telemetry.addData("Target", "Running to %7d ticks", moveTicks);
                telemetry.addData("Current Pos", "LF:%d RF:%d LB:%d RB:%d",
                        leftFront.getCurrentPosition(), rightFront.getCurrentPosition(),
                        leftBack.getCurrentPosition(), rightBack.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion once the target is reached
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);

            // Turn off RUN_TO_POSITION and return to normal mode
            setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    // Helper method to quickly set the mode for all four motors
    private void setMotorMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
        leftBack.setMode(mode);
        rightBack.setMode(mode);
    }
}
