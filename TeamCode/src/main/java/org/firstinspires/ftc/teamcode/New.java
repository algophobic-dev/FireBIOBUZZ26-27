package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Hub Paddle D-Pad Test")
public class New extends LinearOpMode {

    private DcMotor testMotor;

    @Override
    public void runOpMode() {
        testMotor = hardwareMap.get(DcMotor.class, "test_motor");

        telemetry.addData("Status", "Initialized. Press Play.");


        waitForStart();

        while (opModeIsActive()) {
            double motorPower = 0.0;

            // Reading your back paddles mapped to the D-Pad buttons
            if (gamepad1.dpad_up) {
                motorPower = 0.5;  // Right Paddle: Spin forward at half power
            } else if (gamepad1.dpad_down) {
                motorPower = -0.5; // Left Paddle: Spin backward at half power
            } else {
                // Fallback to Left Joystick if no paddles are pressed
                motorPower = -gamepad1.left_stick_y;
            }

            testMotor.setPower(motorPower);

            // Live telemetry diagnostics displayed on the Driver Station phone screen
            telemetry.addData("Motor Power Sent", motorPower);
            telemetry.addData("Right Paddle (D-Pad Up)", gamepad1.dpad_up);
            telemetry.addData("Left Paddle (D-Pad Down)", gamepad1.dpad_down);
            telemetry.addData("Configured port", testMotor.getPortNumber());
            telemetry.addData("Connection", testMotor.getConnectionInfo());
            telemetry.addData("Actual motor power", testMotor.getPower());
            telemetry.update();
        }
    }
}
