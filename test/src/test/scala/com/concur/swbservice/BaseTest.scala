package com.concur.swbservice

import com.concur.swbservice.client.ApiFactory
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.springframework.test.context.{SmartContextLoader, TestContext, TestContextManager}

/**
 * Created by mtalbot on 12/08/2015.
 */
trait BaseTest {

  new TestContextManager(this.getClass()).prepareTestInstance(this)

}
