/*
 *    Copyright (c) 2022-2023.  lWoHvYe(Hongyan Wang)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.unicorn.strategy

import com.lwohvye.sys.modules.system.annotation.UserTypeHandlerAnno
import com.lwohvye.sys.modules.system.strategy.ExtraUserTypeStrategy
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager.getLogger
import org.jetbrains.annotations.BlockingExecutor
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


@Component
@UserTypeHandlerAnno(typeName = "FOUR")
sealed class TKMUserTypeStrategy : ExtraUserTypeStrategy {
    @OptIn(DelicateCoroutinesApi::class)
    override fun grantedAuth(userId: Long): List<GrantedAuthority> {

        println("Start")

        // kotlinx.coroutines将在1.7版本支持JPMS，但在1.7.0-Beta。若以DeBug模式启动，依旧报错 `module kotlin.stdlib does not read module kotlinx.coroutines.core`，Normal run正常
        // https://github.com/Kotlin/kotlinx.coroutines/issues/2237
        // https://github.com/Kotlin/kotlinx.coroutines/pull/3297
        //  Coroutines can perfectly benefit from Loom: A Coroutine always relies on a thread for its execution.
        //  This Thread can also be a VirtualThread. The advantage of having a VirtualThread executing a Coroutine is that all IO operations,
        //  concurrency locks etc. will behave in a non-blocking fashion, wasting no resources.
        // { 参数列表 -> 函数体 } 是 lambda 表达式语法。它用于定义匿名函数，可以作为参数传递给其他函数或方法。
        // 在这个例子中，使用了无参数的 lambda 表达式来定义一个协程，通过 GlobalScope.launch 启动了这个协程。
        // lambda 表达式的主体中包含了一个使用 runBlocking 函数定义的协程，它会暂停当前协程的执行
        // 具体而言就是：
        // 1.GlobalScope.launch { ... }: 创建了一个协程并启动，GlobalScope 是一个全局范围，表示该协程的生命周期与整个应用程序的生命周期相同。
        // { .. } 中定义的是launch函数的block参数，更确叫 launch函数的调用体。 实际上也就是 `suspend CoroutineScope.() -> Unit` 中的 CoroutinesScope.() -> Unit 这个lambda表达式
        // 参数拥有默认值后，变为可选参数
        GlobalScope.launch {
            // 2.runBlocking { ... }: 用于等待协程内部的代码执行完毕后再执行后面的代码。在这个例子中，使用了 delay 函数模拟一个耗时操作，它会暂停协程的执行一段时间，这里是 1000 毫秒（1 秒）。
            runBlocking {
                delay(1000)
            }
            // 3.println("Hello"): 该代码会在 delay 函数执行完毕后被执行，打印 "Hello" 到控制台。
            println("Hello")
        }

        Thread.sleep(2000) // 等待 2 秒钟
        println("Stop")
        //_____________

        val result = GlobalScope.async {
            workload(16) // 调用函数
        }
        runBlocking {
            println("async result is ${result.await()}")
        }
        // 抛去第一个参数的定义，下面这个与上面这个是等同的，只是因为 `Lambda argument should be moved out of parentheses` 所以把lambda移到了后面
        /*runBlocking(EmptyCoroutineContext, {
            println("async result is ${result.await()}")
        })*/
        // 对于存在可变参数的，可通过 参数名 = xxx 对特定参数赋值
        /*runBlocking(block = {
            println("async result is ${result.await()}")
        })*/

        loomCarrier()
        coroutinesDirect()
        //_____________

        return emptyList()
    }

    // 这段代码是一个Kotlin协程中的挂起函数（Suspending Function），使用了suspend关键字修饰。
    // 协程中的挂起函数使用suspend关键字修饰，这意味着当调用该函数时，它会暂停当前协程（所以调用代码后的部分不会执行）而不会阻塞当前线程，从而实现异步执行。
    // 方法只有一行时，可以用 = 代替 { ... }
    suspend fun workload(n: Int): Int { // 使用suspend修饰
        delay(1000)
        return n + 2
    }

    // Define a Custom Dispatcher
    // What if instead of blocking a regular thread, we run it on one of Project Loom’s virtual threads,
    // effectively turning the blocking code into something non-blocking while still being Coroutine compatible?
    val Dispatchers.LOOM: @BlockingExecutor CoroutineDispatcher // 这里的Dispatchers.LOOM 是一个field，field可以有get/set方法
        get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    // use Loom. This will need some time to warm up, overhead. 7s for 1_000_000, Debug Mode 17s for 100_000，比coroutines慢了太多了
    @OptIn(ExperimentalTime::class)
    fun loomCarrier() = runBlocking {

        val log = getLogger()

        measureTime {
            supervisorScope {
                repeat(100_000) {
                    launch(Dispatchers.LOOM) {
                        Thread.sleep(1000)
                    }
                }
            }
        }.also(log::info)

        Unit

    }

    // use Kotlinx Coroutines. While this is ballpark. 4.5s for 1_000_000, Debug Mode 5.2s for 100_000
    // 这段代码的作用是使用 Kotlin 协程实现了 100,000 个并发任务，并度量了执行时间，最后将执行时间输出到日志中。其中 supervisorScope 监控了所有协程的执行情况，避免了某个协程出错后导致整个任务失败的情况。
    // @OptIn(ExperimentalTime::class)：这是一个注解，表示使用了实验性质的时间库。实验性质的库需要在代码中明确声明才能使用。
    @OptIn(ExperimentalTime::class)
    // fun coroutinesDirect() = runBlocking { ... }：这是一个 Kotlin 函数，名为 coroutinesDirect。它使用了协程框架，并在 runBlocking 块中执行了一些并发任务。
    fun coroutinesDirect() = runBlocking {
        // val log = getLogger()：这行代码声明了一个变量 log，它是一个日志记录器。这里使用了某个日志库的函数 getLogger()，获取一个日志记录器实例。
        val log = getLogger()
        // measureTime { ... }.also(log::info)：这是一个计时操作，用来度量代码块的执行时间。它的返回值是执行时间，类型为 kotlin.time.Duration。.also(log::info) 表示执行计时操作的同时，将计时结果输出到日志中。
        measureTime {
            // supervisorScope { ... }：这是一个协程作用域，用于启动一组并发任务，并监控这些任务的执行情况。其中 repeat(100_000) 表示要执行 100,000 次下面的代码块。
            supervisorScope {
                repeat(100_000) {
                    // launch { delay(1000) }：这是一个协程启动函数，用于启动一个新的协程，并在其中执行代码块。这里的代码块是 delay(1000)，表示在协程中延迟 1 秒。
                    launch {
                        delay(1000)
                    }
                }
            }
        }.also(log::info)
        // Unit：这是一个 Kotlin 中的单例对象，表示空值。在这里，它作为 runBlocking 块的返回值，因为 runBlocking 块需要一个返回值。
        Unit

    }
}
