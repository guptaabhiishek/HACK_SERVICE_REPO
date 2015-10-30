package com.concur.swbservice

import com.concur.swbservice.service.Application
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FlatSpec}
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.springframework.boot.test.{SpringApplicationConfiguration, WebIntegrationTest}
import org.springframework.test.context.{TestContextManager, ActiveProfiles, ContextConfiguration}
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import com.concur.swbservice.client.ApiFactory

/**
 * Created by mtalbot on 14/08/2015.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@SpringApplicationConfiguration(classes = Array(classOf[Application], classOf[ApiFactoryHolder]))
@ActiveProfiles(Array("Test-Mocked"))
@WebIntegrationTest(randomPort = true)
abstract class BaseFlatSpec extends FlatSpec with Matchers with BaseTest {

}
