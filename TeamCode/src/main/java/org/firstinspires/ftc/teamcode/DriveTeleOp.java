package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Drive TeleOp")
public class DriveTeleOp extends LinearOpMode {
    // ================= TUNING - edit these, then Run ▶ =================
    static final double MAX_POWER        = 0.8;  // normal top speed
    static final double BOOST_POWER      = 1.0;  // top speed while holding LT
    static final double SLOW_POWER       = 0.4;  // speed while holding RB
    static final double LOCK_POWER       = 1.0;  // how hard it fights pushes when locked
    static final double LOCK_STIFFNESS   = 25.0; // default is 10, higher = fights harder (lower if wheels buzz)
    static final double STRAFE_MULT      = 1.1;  // boosts strafe (mecanum strafes slower)
    static final double STRAFE_TURN_FIX  = 0.0;  // + or - to stop turning while strafing
    static final double STRAFE_DRIFT_FIX = 0.0;  // + or - to stop drifting fwd/back while strafing
    static final double FRONT_SCALE      = 1.0;  // power multiplier for front wheels
    static final double BACK_SCALE       = 1.0;  // power multiplier for back wheels
    static final double INTAKE_POWER     = 1.0;  // intake speed at full RT (gamepad 2)
    static final boolean INTAKE_REVERSED = true;  // flip if intake spins the wrong way
    static final double INTAKE2_POWER     = 1.0;  // intakeMotorTwo speed (gamepad 2)
    static final boolean INTAKE2_REVERSED = false; // flip if intakeMotorTwo spins the wrong way
    static final double OUTTAKE_Y_POWER  = 0.4;  // Y = lowest outtake power (gamepad 2)
    static final double OUTTAKE_B_POWER  = 0.6;  // B
    static final double OUTTAKE_A_POWER  = 0.8;  // A
    static final double OUTTAKE_X_POWER  = 1.0;  // X = highest
    static final boolean OUTTAKE_REVERSED = false; // flip if outtake spins counterclockwise
    // ====================================================================

    @Override
    public void runOpMode() {
        DcMotor leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rightBack  = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor[] motors = {leftFront, leftBack, rightFront, rightBack};

        DcMotor intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setDirection(INTAKE_REVERSED ? DcMotorSimple.Direction.REVERSE
                : DcMotorSimple.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        DcMotor intakeMotorTwo = hardwareMap.get(DcMotor.class, "intakeMotorTwo");
        intakeMotorTwo.setDirection(INTAKE2_REVERSED ? DcMotorSimple.Direction.REVERSE
                : DcMotorSimple.Direction.FORWARD);
        intakeMotorTwo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        DcMotor outtakeMotor = hardwareMap.get(DcMotor.class, "outtakePollen");
        outtakeMotor.setDirection(OUTTAKE_REVERSED ? DcMotorSimple.Direction.REVERSE
                : DcMotorSimple.Direction.FORWARD);
        outtakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // coast down, don't slam stop

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        for (DcMotor m : motors) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        boolean locked = false;
        boolean lockButtonWas = false;

        double outtakePower = 0;  // 0 = off
        boolean yWas = false, bWas = false, aWas = false, xWas = false;

        waitForStart();
        while (opModeIsActive()) {
            // ===== INTAKE (gamepad 2): RT = intake, hold RB = reverse (works even while locked) =====
            double intakePower = gamepad2.right_trigger * INTAKE_POWER;   // 0 to INTAKE_POWER
            if (gamepad2.right_bumper) intakePower = -INTAKE_POWER;       // RB = spit out
            intakeMotor.setPower(intakePower);
            telemetry.addData("intake", "%.2f", intakePower);

            // ===== INTAKE 2 (gamepad 2): LT = forward, hold LB = backward =====
            double intake2Power = gamepad2.left_trigger * -INTAKE2_POWER;  // LT = forward
            if (gamepad2.left_bumper) intake2Power = INTAKE2_POWER;      // LB = backward
            intakeMotorTwo.setPower(intake2Power);
            telemetry.addData("intake 2", "%.2f", intake2Power);

            // ===== OUTTAKE (gamepad 2): Y < B < A < X power. Press = on at that power, same button again = off =====
            boolean yNow = gamepad2.y, bNow = gamepad2.b, aNow = gamepad2.a, xNow = gamepad2.x;
            if (yNow && !yWas) outtakePower = (outtakePower == OUTTAKE_Y_POWER) ? 0 : OUTTAKE_Y_POWER;
            if (bNow && !bWas) outtakePower = (outtakePower == OUTTAKE_B_POWER) ? 0 : OUTTAKE_B_POWER;
            if (aNow && !aWas) outtakePower = (outtakePower == OUTTAKE_A_POWER) ? 0 : OUTTAKE_A_POWER;
            if (xNow && !xWas) outtakePower = (outtakePower == OUTTAKE_X_POWER) ? 0 : OUTTAKE_X_POWER;
            yWas = yNow; bWas = bNow; aWas = aNow; xWas = xNow;
            outtakeMotor.setPower(outtakePower);
            telemetry.addData("outtake", outtakePower == 0 ? "OFF" : String.format("%.2f", outtakePower));

            // ===== LOCK: click the RIGHT STICK to toggle lock on/off =====
            boolean lockButton = gamepad1.right_stick_button;

            if (lockButton && !lockButtonWas) {   // only on the moment it's pressed
                locked = !locked;
                for (DcMotor m : motors) {
                    DcMotorEx mx = (DcMotorEx) m;
                    if (locked) {                 // lock right here
                        mx.setTargetPosition(mx.getCurrentPosition());
                        mx.setTargetPositionTolerance(2);          // fight even tiny pushes
                        mx.setPositionPIDFCoefficients(LOCK_STIFFNESS);
                        mx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        mx.setPower(LOCK_POWER);
                    } else {                      // unlock -> back to driving
                        mx.setPower(0);
                        mx.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    }
                }
            }
            lockButtonWas = lockButton;

            if (locked) {
                telemetry.addLine("LOCKED - click right stick to unlock");
                telemetry.update();
                continue; // ignore sticks while locked
            }

            // ===== DRIVE: LEFT stick = drive + strafe, RIGHT stick = turn =====
            double y  = -gamepad1.left_stick_y;
            double x  =  gamepad1.left_stick_x * STRAFE_MULT;
            double rx =  gamepad1.right_stick_x;

            // Strafe corrections
            rx += x * STRAFE_TURN_FIX;
            y  += x * STRAFE_DRIFT_FIX;

            double lf = (y + x + rx) * FRONT_SCALE;
            double lb = (y - x + rx) * BACK_SCALE;
            double rf = (y - x - rx) * FRONT_SCALE;
            double rb = (y + x - rx) * BACK_SCALE;

            double max = Math.max(1.0, Math.max(Math.max(Math.abs(lf), Math.abs(lb)),
                    Math.max(Math.abs(rf), Math.abs(rb))));

            double speed;
            if (gamepad1.right_bumper) {
                speed = SLOW_POWER;                                   // RB = slow
            } else {
                double lt = gamepad1.left_trigger;                    // 0.0 to 1.0
                speed = MAX_POWER + (BOOST_POWER - MAX_POWER) * lt;   // LT = boost
            }

            leftFront.setPower(lf / max * speed);
            leftBack.setPower(lb / max * speed);
            rightFront.setPower(rf / max * speed);
            rightBack.setPower(rb / max * speed);

            telemetry.addData("speed", speed);
            telemetry.addData("LF/LB/RF/RB", "%.2f %.2f %.2f %.2f",
                    lf / max * speed, lb / max * speed, rf / max * speed, rb / max * speed);
            telemetry.update();
        }
    }
}