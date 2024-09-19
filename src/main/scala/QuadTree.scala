sealed trait QuadTree
case class InternalNode(centerOfMass: (Double, Double), totalMass: Double, nw: QuadTree, ne: QuadTree, sw: QuadTree, se: QuadTree) extends QuadTree
case class Leaf(particle: Particle) extends QuadTree
case object Empty extends QuadTree

def insert(particle: Particle, node: QuadTree, bounds: (Double, Double, Double, Double)): QuadTree = {
  val (xmin, ymin, xmax, ymax) = bounds
  val minSize = 1e-3  // Set a minimum size for subdivision

  // Prevent further subdivision if the region is too small
  if ((xmax - xmin) < minSize || (ymax - ymin) < minSize) {
    return Leaf(particle)
  }

  node match {
    case Empty => Leaf(particle)
    case Leaf(p) =>
      // If the particles are too close, merge them
      if (math.abs(p.x - particle.x) < minSize && math.abs(p.y - particle.y) < minSize) {
        val totalMass = p.mass + particle.mass
        val newX = (p.x * p.mass + particle.x * particle.mass) / totalMass
        val newY = (p.y * p.mass + particle.y * particle.mass) / totalMass
        Leaf(p.copy(x = newX, y = newY, mass = totalMass))
      } else {
        // Subdivide the region and insert both particles
        val midx = (xmin + xmax) / 2
        val midy = (ymin + ymax) / 2
        val internalNode = InternalNode((0, 0), 0, Empty, Empty, Empty, Empty)
        insert(particle, insert(p, internalNode, bounds), bounds)
      }

    case InternalNode(centerOfMass, totalMass, nw, ne, sw, se) =>
      val midx = (xmin + xmax) / 2
      val midy = (ymin + ymax) / 2
      val updatedNode = if (particle.x < midx && particle.y < midy) {
        InternalNode(centerOfMass, totalMass, insert(particle, nw, (xmin, ymin, midx, midy)), ne, sw, se)
      } else if (particle.x >= midx && particle.y < midy) {
        InternalNode(centerOfMass, totalMass, nw, insert(particle, ne, (midx, ymin, xmax, midy)), sw, se)
      } else if (particle.x < midx && particle.y >= midy) {
        InternalNode(centerOfMass, totalMass, nw, ne, insert(particle, sw, (xmin, midy, midx, ymax)), se)
      } else {
        InternalNode(centerOfMass, totalMass, nw, ne, sw, insert(particle, se, (midx, midy, xmax, ymax)))
      }
      // Recalculate the center of mass and total mass
      val newTotalMass = totalMass + particle.mass
      val newCenterOfMass = (
        (centerOfMass._1 * totalMass + particle.x * particle.mass) / newTotalMass,
        (centerOfMass._2 * totalMass + particle.y * particle.mass) / newTotalMass
      )
      updatedNode.copy(centerOfMass = newCenterOfMass, totalMass = newTotalMass)
  }
}

def computeForce(particle: Particle, node: QuadTree, bounds: (Double, Double, Double, Double), theta: Double = 0.5): (Double, Double) = {
  val (xmin, ymin, xmax, ymax) = bounds
  node match {
    case Empty => (0, 0)
    case Leaf(p) =>
      if (p != particle) {
        particle.calculateForce(p.x, p.y, p.mass) // Direct force calculation
      } else {
        (0, 0)
      }
    case InternalNode(centerOfMass, totalMass, nw, ne, sw, se) =>
      val dx = centerOfMass._1 - particle.x
      val dy = centerOfMass._2 - particle.y
      val distance = math.sqrt(dx * dx + dy * dy)
      val s = xmax - xmin // Size of the region

      // If s / distance < theta, treat the internal node as a single body
      if (s / distance < theta) {
        particle.calculateForce(centerOfMass._1, centerOfMass._2, totalMass)
      } else {
        // Otherwise, compute forces from child nodes
        val fNW = computeForce(particle, nw, (xmin, ymin, (xmin + xmax) / 2, (ymin + ymax) / 2), theta)
        val fNE = computeForce(particle, ne, ((xmin + xmax) / 2, ymin, xmax, (ymin + ymax) / 2), theta)
        val fSW = computeForce(particle, sw, (xmin, (ymin + ymax) / 2, (xmin + xmax) / 2, ymax), theta)
        val fSE = computeForce(particle, se, ((xmin + xmax) / 2, (ymin + ymax) / 2, xmax, ymax), theta)
        (
          fNW._1 + fNE._1 + fSW._1 + fSE._1,
          fNW._2 + fNE._2 + fSW._2 + fSE._2
        )
      }
  }
}
