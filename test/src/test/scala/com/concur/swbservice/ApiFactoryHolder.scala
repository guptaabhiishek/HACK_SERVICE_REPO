package com.concur.swbservice

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.{ApplicationContextAware, ApplicationContext}
import org.springframework.context.annotation.Configuration
import com.concur.swbservice.client.ApiFactory

/**
 * Created by mtalbot on 22/10/2015.
 */

object ApiFactoryHolder {
  protected var apiFactory: Option[ApiFactory] = None
  protected var context: ApplicationContext = null
  protected var portNo: Int = 0

  def port = portNo.toString

  def factory = {
    if (apiFactory.isEmpty) {
      portNo = context.getEnvironment.getProperty("local.server.port", classOf[Int])
      apiFactory = Some(new ApiFactory("http://localhost:" + port, 10000))
    }

    apiFactory.get
  }
}

@Configuration
class ApiFactoryHolder extends ApplicationContextAware {

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = ApiFactoryHolder.context = applicationContext;

}
