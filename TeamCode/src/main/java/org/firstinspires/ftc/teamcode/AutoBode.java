package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "AutoBode", group = "Autonomous")
public class AutoBode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // Starting position
        Pose2d startPose = new Pose2d(0, 0, 0);

        // Create drivetrain
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        waitForStart();

        if (isStopRequested()) return;

        // Autonomous path
        Actions.runBlocking(
                drive.actionBuilder(startPose)

                        // Move forward 1 meter (39.37 inches)
                        .strafeTo(new Vector2d(39.37, 0))

                        // Turn 90 degrees counter-clockwise
                        .turn(Math.toRadians(90))

                        // Move forward another 1 meter
                        .strafeTo(new Vector2d(39.37, 39.37))

                        .build()
        );
    }
}