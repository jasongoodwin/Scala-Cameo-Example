package com.example

import akka.actor.{Props, ActorSystem}

import scala.concurrent.{Promise, Future}

/**
 * This project demonstrates an Ask-free actor system that will deliver results in a future.
 * This is more efficient than using ask -
 * Ask would create 3 futures and 3 temporary actors .
 * Cameo reduces that overhead to 1 future and 1 temporary actor.
 */

object ApplicationMain extends App {
  val system = ActorSystem("MyActorSystem")
  val profileStore = system.actorOf(Props[UserProfileStore], "userProfileStore")

  demonstrateCameo()
  
  def demonstrateCameo() {
    implicit val ec = system.dispatcher
    val usersToGet = List("jay", "gaetan", "damien", "chris")
    getProfiles(usersToGet).foreach(x => {
      println("got the following profiles: " + x)
    })
  }

  def getProfiles(names: List[String]): Future[List[Profile]] = {
    val promise = Promise[List[Profile]]()

    val cameo = system.actorOf(Props(classOf[UserProfileStoreCameo], promise, names.size))

    names.foreach(x => profileStore.tell(GetProfile(x), cameo))

    promise.future
  }
}