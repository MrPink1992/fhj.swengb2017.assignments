package at.fhj.swengb.apps.calculator

import scala.util.{Try, Success, Failure}

/**
  * Companion object for our reverse polish notation calculator.
  */
object RpnCalculator {

  /**
    * Returns empty RpnCalculator if string is empty, otherwise pushes all operations
    * on the stack of the empty RpnCalculator.
    *
    * @param s a string representing a calculation, for example '1 2 +'
    * @return
    */
  def apply(s: String): Try[RpnCalculator] = {
    if (s == "") {
      return Success(RpnCalculator())
    }

    var controller = RpnCalculator()

    for (str <- s.split(" ")) {
      val op = Op(str)

      if (controller.push(op).isSuccess) {
        controller = controller.push(op).get
      } else {
        return controller.push(op)
      }
    }

    return Success(controller)
  }
}

/**
  * Reverse Polish Notation Calculator.
  *
  * @param stack a datastructure holding all operations
  */
case class RpnCalculator(stack: List[Op] = Nil) {

  /**
    * By pushing Op on the stack, the Op is potentially executed. If it is a Val, it the op instance is just put on the
    * stack, if not then the stack is examined and the correct operation is performed.
    *
    * @param op
    * @return
    */
  def push(op: Op): Try[RpnCalculator] = {
    def operate(v: (Val, Val) => Val): Try[RpnCalculator]= try {
      val (last, lastC) = pop
      val (bLast, bLastC) = lastC.pop
      (last, bLast) match {
        case (left: Val, right: Val) => bLastC.push(v(left, right))
        case _ => Failure(new NoSuchElementException)
      }
    } catch {
      case e: NoSuchElementException => Failure(e)
    }

    op match {
      case v: Val => Success(RpnCalculator(v :: stack))
      case Mul => operate(Mul.eval)
      case Sub => operate(Sub.eval)
      case Add => operate(Add.eval)
      case Div => operate(Div.eval)
    }
  }

  /**
    * Pushes val's on the stack.
    *
    * If op is not a val, pop two numbers from the stack and apply the operation.
    *
    * @param op
    * @return
    */
  def push(op: Seq[Op]): Try[RpnCalculator] = {
    op match {
      case h :: Nil => push(h)
      case h :: t => push(h) match {
        case Success(c) => c.push(t)
        case Failure(e) => Failure(e)
      }
    }
  }

  /**
    * Returns an tuple of Op and a RevPolCal instance with the remainder of the stack.
    *
    * @return
    */
  def pop(): (Op, RpnCalculator) = (peek(), RpnCalculator(stack.take(stack.length - 1)))

  /**
    * If stack is nonempty, returns the top of the stack. If it is empty, this function throws a NoSuchElementException.
    *
    * @return
    */
  def peek(): Op = {
    if (stack.nonEmpty) {
      stack.last
    } else {
      throw new NoSuchElementException
    }
  }

  /**
    * returns the size of the stack.
    *
    * @return
    */
  def size: Int = stack.count(_.isInstanceOf[Val])
}