package edu.berkeley.cs.boom.bloomscala.codegen.dataflow

import edu.berkeley.cs.boom.bloomscala.analysis.Stratum
import scala.collection.mutable

/**
 * Represents a generic push-based dataflow element.
 */
class DataflowElement(implicit graph: DataflowGraph, implicit val stratum: Stratum) {
  /** A unique identifier for this dataflow element */
  val id = graph.nextElementId.getAndIncrement

  val inputPorts = mutable.HashSet[InputPort]()
  val outputPorts = mutable.HashSet[OutputPort]()

  def upstreamElements = inputPorts.flatMap(ip => ip.connectedPorts.map(op => op.elem)).toSet
  def downstreamElements = outputPorts.flatMap(op => op.connectedPorts.map(ip => ip.elem)).toSet

  // This statement needs to be AFTER we assign the id so that hashCode()
  // and equals() return the right results when we add this element to the
  // hashSet:
  graph.elements += this

  override def equals(other: Any): Boolean = other match {
    case that: DataflowElement => id == that.id
    case _ => false
  }

  override def hashCode(): Int = id
}

/**
 * Mixin trait for dataflow elements that maintain internal state
 * that must be invalidated if any of their inputs perform rescans.
 */
trait Stateful

/**
 * Mixin trait for stateful dataflow elements that can perform
 * rescans out of their caches rather than having to rescan
 * their inputs.
 */
trait Rescanable extends Stateful