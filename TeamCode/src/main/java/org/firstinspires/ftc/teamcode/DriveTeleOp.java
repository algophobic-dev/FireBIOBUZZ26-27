package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Drive TeleOp")
public class DriveTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Names must match your Driver Hub config (same as in PedroConstants)
        DcMotor leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rightBack  = hardwareMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        for (DcMotor m : new DcMotor[]{leftFront, leftBack, rightFront, rightBack}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        waitForStart();
        while (opModeIsActive()) {
            // RIGHT stick = drive + strafe
            double y  = -gamepad1.right_stick_y;      // forward/back
            double x  =  gamepad1.right_stick_x * 1.1; // strafe left/right
            // LEFT stick = turn
            double rx =  gamepad1.left_stick_x;       // turn left/right

            double speed = gamepad1.right_bumper ? 0.4 : 1.0; // hold RB = slow mode

            double max = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            leftFront.setPower((y + x + rx) / max * speed);
            leftBack.setPower((y - x + rx) / max * speed);
            rightFront.setPower((y - x - rx) / max * speed);
            rightBack.setPower((y + x - rx) / max * speed);

            // D-pad up/down - reserved for later
            if (gamepad1.dpad_up) {
                // TODO: dpad up action
            }
            if (gamepad1.dpad_down) {
                // TODO: dpad down action
            }

            telemetry.addData("slow mode", gamepad1.right_bumper);
            telemetry.update();
        }
    }
}