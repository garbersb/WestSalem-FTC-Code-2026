package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutoBode", group = "Autonomous")
public class AutoBode extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    // CHANGE THIS after calibration
    // This is approximately the number of motor encoder ticks
    // needed to move the robot 1 meter.
    static final int TICKS_PER_METER = 1000;

    @Override
    public void runOpMode() {

        // Get motors from the Robot Configuration
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Set motor directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        // Make the robot stop when power is removed
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        if (isStopRequested()) return;

        // ==========================================
        // MOVE FORWARD 1 METER
        // ==========================================

        driveForward(TICKS_PER_METER, 0.5);

        // ==========================================
        // TURN 90 DEGREES
        // ==========================================

        turn90(0.5);

        // ==========================================
        // MOVE FORWARD ANOTHER 1 METER
        // ==========================================

        driveForward(TICKS_PER_METER, 0.5);

        // Stop
        stopMotors();
    }


    // ==========================================
    // DRIVE FORWARD
    // ==========================================

    private void driveForward(int ticks, double power) {

        frontLeft.setTargetPosition(
                frontLeft.getCurrentPosition() + ticks);

        frontRight.setTargetPosition(
                frontRight.getCurrentPosition() + ticks);

        backLeft.setTargetPosition(
                backLeft.getCurrentPosition() + ticks);

        backRight.setTargetPosition(
                backRight.getCurrentPosition() + ticks);

        // Tell the motors to run to their targets
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Power must be positive in RUN_TO_POSITION
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        // Wait until all motors reach their targets
        while (opModeIsActive() &&
                (frontLeft.isBusy() ||
                        frontRight.isBusy() ||
                        backLeft.isBusy() ||
                        backRight.isBusy())) {

            telemetry.addData("FL", frontLeft.getCurrentPosition());
            telemetry.addData("FR", frontRight.getCurrentPosition());
            telemetry.addData("BL", backLeft.getCurrentPosition());
            telemetry.addData("BR", backRight.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();
    }


    // ==========================================
    // TURN 90 DEGREES
    // ==========================================

    private void turn90(double power) {

        int turnTicks = 500; // CHANGE THIS AFTER CALIBRATION

        frontLeft.setTargetPosition(
                frontLeft.getCurrentPosition() + turnTicks);

        backLeft.setTargetPosition(
                backLeft.getCurrentPosition() + turnTicks);

        frontRight.setTargetPosition(
                frontRight.getCurrentPosition() - turnTicks);

        backRight.setTargetPosition(
                backRight.getCurrentPosition() - turnTicks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        while (opModeIsActive() &&
                (frontLeft.isBusy() ||
                        frontRight.isBusy() ||
                        backLeft.isBusy() ||
                        backRight.isBusy())) {

            telemetry.addData("Turning", "90 degrees");
            telemetry.update();
        }

        stopMotors();
    }


    // ==========================================
    // STOP MOTORS
    // ==========================================

    private void stopMotors() {

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}