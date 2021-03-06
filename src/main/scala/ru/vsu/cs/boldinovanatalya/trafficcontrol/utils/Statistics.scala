package ru.vsu.cs.boldinovanatalya.trafficcontrol.utils


import ru.vsu.cs.boldinovanatalya.trafficcontrol.geneticalgorithm.TrainingElement
import ru.vsu.cs.traffic.Color.{YELLOW, RED, GREEN}
import ru.vsu.cs.traffic.Direction.FORWARD
import ru.vsu.cs.traffic.event.{ModelActed, ColorChanged, BeforeColorChanged, VehicleSpawned}
import ru.vsu.cs.traffic.{Color, Point, TrafficModel}

class Statistics(model: TrafficModel) {
  private val MinSpeed = 3

  private var approachingGreen_ = 0.0
  private var _maxQueuing = 0.0
  private var _maxApproaching = 0.0

  def maxQueuing = _maxQueuing

  def maxApproaching = _maxApproaching

  model.vehicleEventHandlers += {
    case VehicleSpawned(vehicle) =>
      if (model.trafficLights.filter(_.color == GREEN).map(_(FORWARD)).contains(vehicle.trafficFlow)) {
        approachingGreen_ += 1
      }
    case _ => Unit
  }

  model.trafficLightEventHandlers += {
    case ColorChanged(tl) =>
      approachingGreen_ = 0
    case _ => Unit
  }

  model.trafficModelEventHandlers += {
    case ModelActed(_) =>
      if (approachingGreen > _maxApproaching) _maxApproaching = approachingGreen
      val currentQueuing = math.max(queuingGreen, queuingRed)
      if (currentQueuing > _maxQueuing) _maxQueuing = currentQueuing
  }

  def approachingGreen = {
    approachingGreen_
  }

  private def queuing(color: Color): Double = {
    model.trafficLights.filter(_.color == color).map(_(FORWARD).vehicles.count(_.speed <= MinSpeed)).sum
  }

  def queuingGreen = queuing(GREEN)

  def queuingRed = queuing(RED)


}
