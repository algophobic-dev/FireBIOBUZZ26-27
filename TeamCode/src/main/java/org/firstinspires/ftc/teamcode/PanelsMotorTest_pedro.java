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
    public static String MOTOR_NAME = "testMotor"; // change live in Panels
    public static double POWER = 0.0;              // change live in Panels

    @Override
    public void runOpMode() {
        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();
        DcMotorEx motor = null;
        String currentName = "";

        waitForStart();
        while (opModeIsActive()) {
            // Switch motors when the name changes in Panels
            if (!MOTOR_NAME.equals(currentName)) {
                if (motor != null) motor.setPower(0); // stop the old one
                motor = hardwareMap.tryGet(DcMotorEx.class, MOTOR_NAME);
                currentName = MOTOR_NAME;
            }

            panels.debug("motors in config: " + hardwareMap.getAllNames(DcMotorEx.class));

            if (motor == null) {
                panels.debug("NO MOTOR named \"" + MOTOR_NAME + "\" - check spelling");
            } else {
                motor.setPower(POWER);
                panels.debug("motor: " + MOTOR_NAME + " (port " + motor.getPortNumber() + ")");
                panels.debug("power: " + POWER);
                panels.debug("position: " + motor.getCurrentPosition());
                panels.debug("velocity: " + motor.getVelocity());
            }
            panels.update(telemetry);
        }
        if (motor != null) motor.setPower(0);
    }
}