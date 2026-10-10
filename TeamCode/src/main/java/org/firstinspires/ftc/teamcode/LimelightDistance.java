package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

@TeleOp(name = "Limelight Distance v2")
public class LimelightDistance extends LinearOpMode {
    @Override
    public void runOpMode() {
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0); // your AprilTag pipeline
        limelight.start();

        telemetry.addLine("Ready - press Start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            telemetry.addData("result null?", result == null);

            if (result != null) {
                telemetry.addData("valid?", result.isValid());
                telemetry.addData("pipeline", result.getPipelineIndex());
                telemetry.addData("tags seen", result.getFiducialResults().size());

                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                    Position p = tag.getTargetPoseCameraSpace().getPosition(); // meters
                    double meters = Math.sqrt(p.x * p.x + p.y * p.y + p.z * p.z);
                    telemetry.addData("tag " + tag.getFiducialId() + " xyz (m)",
                            "%.2f, %.2f, %.2f", p.x, p.y, p.z);
                    telemetry.addData("tag " + tag.getFiducialId() + " DISTANCE",
                            "%.1f in  (%.2f m)", meters * 39.37, meters);
                }
            }
            telemetry.update();
        }
        limelight.stop();
    }
}