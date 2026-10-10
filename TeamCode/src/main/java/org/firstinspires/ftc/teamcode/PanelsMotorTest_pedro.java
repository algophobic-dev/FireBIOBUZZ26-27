package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Configurable
@TeleOp(name = "Panels Motor Test")
public class PanelsMotorTest_pedro extends LinearOpMode {
    public static String MOTOR_NAME = "leftFront"; // name from your config
    public static double POWER = 0.0;              // change live in Panels

    @Override
    public void runOpMode() {
        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);

        waitForStart();
        while (opModeIsActive()) {
            motor.setPower(POWER);
            panels.debug("power: " + POWER);
            panels.debug("position: " + motor.getCurrentPosition());
            panels.debug("velocity: " + motor.getVelocity());
            panels.update(telemetry);
        }
    }
}