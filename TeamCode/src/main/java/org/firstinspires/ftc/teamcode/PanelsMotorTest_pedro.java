package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@TeleOp(name = "Panels Motor Test")
public class PanelsMotorTest_pedro extends LinearOpMode {
    // ---- Motor (change live in Panels) ----
    public static String MOTOR_NAME = "testMotor";
    public static double POWER = 0.0;

    // ---- Servo (change live in Panels) ----
    public static String SERVO_NAME = "limelightServo";
    public static boolean SERVO_ENABLED = false; // servo won't move until true
    public static double SERVO_POSITION = 0.5;   // 0.0 to 1.0
    public static boolean SERVO_REVERSED = false;

    @Override
    public void runOpMode() {
        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();
        DcMotorEx motor = null;
        Servo servo = null;
        String currentMotor = "";
        String currentServo = "";

        waitForStart();
        while (opModeIsActive()) {
            // ===== MOTOR =====
            if (!MOTOR_NAME.equals(currentMotor)) {
                if (motor != null) motor.setPower(0);
                motor = hardwareMap.tryGet(DcMotorEx.class, MOTOR_NAME);
                currentMotor = MOTOR_NAME;
            }
            panels.debug("motors in config: " + hardwareMap.getAllNames(DcMotorEx.class));
            if (motor == null) {
                panels.debug("NO MOTOR named \"" + MOTOR_NAME + "\"");
            } else {
                motor.setPower(POWER);
                panels.debug("motor: " + MOTOR_NAME + " (port " + motor.getPortNumber() + ")");
                panels.debug("power: " + POWER);
                panels.debug("position: " + motor.getCurrentPosition());
                panels.debug("velocity: " + motor.getVelocity());
            }

            // ===== SERVO =====
            if (!SERVO_NAME.equals(currentServo)) {
                servo = hardwareMap.tryGet(Servo.class, SERVO_NAME);
                currentServo = SERVO_NAME;
            }
            panels.debug("servos in config: " + hardwareMap.getAllNames(Servo.class));
            if (servo == null) {
                panels.debug("NO SERVO named \"" + SERVO_NAME + "\"");
            } else {
                servo.setDirection(SERVO_REVERSED ? Servo.Direction.REVERSE : Servo.Direction.FORWARD);
                double pos = Math.max(0.0, Math.min(1.0, SERVO_POSITION));
                if (SERVO_ENABLED) servo.setPosition(pos);
                panels.debug("servo: " + SERVO_NAME + " (port " + servo.getPortNumber() + ")");
                panels.debug("servo enabled: " + SERVO_ENABLED + "  target: " + pos);
            }

            panels.update(telemetry);
        }
        if (motor != null) motor.setPower(0);
    }
}