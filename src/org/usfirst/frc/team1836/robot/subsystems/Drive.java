package org.usfirst.frc.team1836.robot.subsystems;

import org.usfirst.frc.team1836.robot.Constants;
import org.usfirst.frc.team1836.robot.Inputs;
import org.usfirst.frc.team1836.robot.util.MkCANTalon;
import org.usfirst.frc.team1836.robot.util.Subsystem;
import org.usfirst.frc.team1836.robot.util.TrajectoryPoint;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends Subsystem {

  private static Drive drive;
  private final RobotDrive robotDr;
  final MkCANTalon leftfwdtalon;
  final MkCANTalon leftbacktalon;
  final MkCANTalon rightfwdtalon;
  final MkCANTalon rightbacktalon;
  AHRS navX;
  PIDController turnController;

  private TrajectoryPoint lastPoint;

  public static Drive getInstance() {
    if (drive == null)
      drive = new Drive();
    return drive;
  }

  public Drive() {
    super("Drive");
    navX = new AHRS(SPI.Port.kMXP);
    leftfwdtalon =
        new MkCANTalon(Constants.Hardware.LEFT_FWD_TALON_ID, Constants.Drive.WheelDiameter, false);
    leftbacktalon =
        new MkCANTalon(Constants.Hardware.LEFT_BACK_TALON_ID, Constants.Drive.WheelDiameter, false);
    rightbacktalon = new MkCANTalon(Constants.Hardware.RIGHT_BACK_TALON_ID,
        Constants.Drive.WheelDiameter, false);
    rightfwdtalon =
        new MkCANTalon(Constants.Hardware.RIGHT_FWD_TALON_ID, Constants.Drive.WheelDiameter, false);

    robotDr = new RobotDrive(leftfwdtalon, leftbacktalon, rightfwdtalon, rightbacktalon);

    leftfwdtalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    leftfwdtalon.reverseSensor(false);
    leftfwdtalon.configNominalOutputVoltage(+0.0f, -0.0f);
    leftfwdtalon.configPeakOutputVoltage(+12.0f, -12.0f);
    leftfwdtalon.setProfile(0);
    leftfwdtalon.setF(Constants.PID.DriveF);
    leftfwdtalon.setP(Constants.PID.DriveP);
    leftfwdtalon.setI(Constants.PID.DriveI);
    leftfwdtalon.setD(Constants.PID.DriveD);
    leftfwdtalon.setIZone(Constants.PID.DriveIZone);
    leftfwdtalon.setMotionMagicCruiseVelocity(Constants.PID.DriveV);
    leftfwdtalon.setMotionMagicAcceleration(Constants.PID.DriveA);

    rightfwdtalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    rightfwdtalon.reverseSensor(false);
    rightfwdtalon.configNominalOutputVoltage(+0.0f, -0.0f);
    rightfwdtalon.configPeakOutputVoltage(+12.0f, -12.0f);
    rightfwdtalon.setProfile(0);
    rightfwdtalon.setF(Constants.PID.DriveF);
    rightfwdtalon.setP(Constants.PID.DriveP);
    rightfwdtalon.setI(Constants.PID.DriveI);
    rightfwdtalon.setD(Constants.PID.DriveD);
    rightfwdtalon.setIZone(Constants.PID.DriveIZone);
    rightfwdtalon.setMotionMagicCruiseVelocity(Constants.PID.DriveV);
    rightfwdtalon.setMotionMagicAcceleration(Constants.PID.DriveA);

    leftfwdtalon.setPrint(false);
    leftbacktalon.setPrint(false);
    rightfwdtalon.setPrint(false);
    rightbacktalon.setPrint(false);

    turnController = new PIDController(Constants.PID.TurnP, Constants.PID.TurnI, Constants.PID.TurnD, Constants.PID.TurnF, navX, output -> {
      robotDr.tankDrive(output, -output);
    });
    turnController.setInputRange(-180.0f, 180.0f);
    turnController.setOutputRange(-1.0, 1.0);
    turnController.setAbsoluteTolerance(Constants.PID.TurnTol);
    turnController.setContinuous(true);
  }

  @Override
  public void initTeleop() {
    leftfwdtalon.changeControlMode(TalonControlMode.PercentVbus);
    leftbacktalon.changeControlMode(TalonControlMode.PercentVbus);
    rightfwdtalon.changeControlMode(TalonControlMode.PercentVbus);
    rightbacktalon.changeControlMode(TalonControlMode.PercentVbus);
    leftfwdtalon.setEncPosition(0);
    rightfwdtalon.setEncPosition(0);
  }

  @Override
  public void updateTeleop() {
    robotDr.arcadeDrive(Inputs.driverJoystick.getRawAxis(1), Inputs.driverJoystick.getRawAxis(2));
  }

  @Override
  public void initAuto() {
    leftfwdtalon.changeControlMode(TalonControlMode.MotionMagic);
    leftbacktalon.changeControlMode(TalonControlMode.Follower);
    leftbacktalon.set(Constants.Hardware.LEFT_FWD_TALON_ID);
    rightfwdtalon.changeControlMode(TalonControlMode.MotionMagic);
    rightbacktalon.changeControlMode(TalonControlMode.Follower);
    rightbacktalon.set(Constants.Hardware.RIGHT_BACK_TALON_ID);
    leftfwdtalon.setEncPosition(0);
    rightfwdtalon.setEncPosition(0);
  }

  @Override
  public void updateAuto() {

  }

  @Override
  public void sendToSmartDash() {
    SmartDashboard.putNumber("Left Encoder Position", leftfwdtalon.getEncPosition());
    SmartDashboard.putNumber("Right Encoder Position", rightfwdtalon.getEncPosition());
    SmartDashboard.putNumber("Left Encoder Velocity", leftfwdtalon.getEncVelocity());
    SmartDashboard.putNumber("Right Encoder Velocity", rightfwdtalon.getEncVelocity());
  }

  public void setMotionMagicRelPosition(double pos) {
    leftfwdtalon.setEncPosition(0);
    rightfwdtalon.setEncPosition(0);
    leftfwdtalon.set(pos);
    rightfwdtalon.set(pos);
  }

  public void setMotionMagicAbsPosition(double pos) {
    leftfwdtalon.set(pos);
    rightfwdtalon.set(pos);
  }

  public boolean getMotionMagicOnTarget() {
    if (Math.abs(leftfwdtalon.getClosedLoopError()) <= Constants.Drive.DriveMagicMotionTol
        && Math.abs(rightfwdtalon.getClosedLoopError()) <= Constants.Drive.DriveMagicMotionTol) {
      return true;
    } else {
      return false;
    }

  }

  public void setDriveVelocity(double velocity) {
    leftfwdtalon.changeControlMode(TalonControlMode.Speed);
    rightfwdtalon.changeControlMode(TalonControlMode.Speed);
    leftfwdtalon.set(velocity);
    rightfwdtalon.set(velocity);
  }

  public void setDriveTrajectory(TrajectoryPoint point) {
    if (point.getCount() != 1) {
      double leftVel = point.getVel()
          + ((lastPoint.getPos() - leftfwdtalon.getPosition()) * Constants.PID.DriveFollowerP);
      double rightVel = point.getVel()
          + ((lastPoint.getPos() - rightfwdtalon.getPosition()) * Constants.PID.DriveFollowerP);
      leftfwdtalon.set(leftVel);
      rightfwdtalon.set(rightVel);
    }
    System.out.println(point);
    lastPoint = point;
  }

}
