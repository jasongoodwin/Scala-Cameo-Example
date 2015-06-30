package com.example

import akka.actor.{Actor, ActorLogging}

import scala.collection.immutable.HashMap
import scala.concurrent.Promise
import scala.concurrent.duration._

case class Profile(name: String, city: String) 
case object ProfileNotFound

case class GetProfile(name: String)

class UserProfileStore extends Actor with ActorLogging {
  
  val profileStore = HashMap(
  "jay" -> new Profile("jay", "toronto"),
  "gaetan" -> new Profile("gaetan", "newyork"),
  "chris" -> new Profile("chris", "prince edward county")
  )
  
  def receive = {
  	case GetProfile(x) =>
      sender () ! profileStore.getOrElse(x, ProfileNotFound)
    case x =>
      log.info("unknown message: " + x)
  }
}

class UserProfileStoreCameo(promise: Promise[List[Profile]], numToRecieve: Int) 
  extends Actor with ActorLogging {
  var numResponses = 0
  var responsesList = List.empty[Profile]

  implicit val ec = context.dispatcher
  context.system.scheduler.scheduleOnce(FiniteDuration(1, "second"), self, "timeout")
  
  override def receive = {
    case x: Profile =>
      numResponses += 1
      responsesList = responsesList.+:(x)
      finishIfDone()
    case ProfileNotFound =>
      numResponses += 1
      finishIfDone()
    case "timeout" =>
      log.info("Got timeout!")
      finish()
    case x =>
      log.info("unknown message... " + x)
  } 
  
  def finishIfDone() {
    if(numResponses >= numToRecieve) {
      finish()
    }
  }

  /**
   * Send any responses we have or else fail the future.
   */

  def finish() {
    if(responsesList.isEmpty) promise.failure(new Exception("got no responses"))
    else promise.success(responsesList)
    
    context.stop(self)
  }
}