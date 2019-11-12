package services

import java.util.concurrent.atomic.AtomicInteger

import dao._
import javax.inject._
import play.api.cache.AsyncCacheApi

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * This trait demonstrates how to create a component that is injected
  * into a controller. The trait represents a counter that returns a
  * incremented number each time it is called.
  */

@Singleton
class MyCache @Inject()(geneGoDao: GeneGoDao, basicDao: BasicDao, cache: AsyncCacheApi) {

  basicDao.selectAll.flatMap { x =>
    val ids = x.map(_.id)
    cache.set("basics", x)
    geneGoDao.selectAll(ids)
  }.map { x =>
    cache.set("geneGos", x)
  }


}
