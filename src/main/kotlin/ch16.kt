import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce

class Ch16{
    fun fanInExample() = runBlocking {
        val channel = Channel<String>()
        launch { sendString(channel, "foo", 200L) }
        launch { sendString(channel, "BAR!", 500L) }
        repeat(50){
            println(channel.receive())
        }
        coroutineContext.cancelChildren()
    }

    suspend fun pipelineExample() = coroutineScope {
        val numbers = numbers()
        val squared = square(numbers)
        for (num in squared) println(num)
    }

}

private suspend fun sendString(
    channel: SendChannel<String>,
    text:String,
    time:Long
){
    while(true){
        delay(time)
        channel.send(text)
    }
}

private fun <T> CoroutineScope.fanIn(channels:List<ReceiveChannel<T>>):ReceiveChannel<T> = produce{
    for(channel in channels){
        launch {
            for(element in channel){
                send(element)
            }
        }
    }
}

private fun CoroutineScope.numbers():ReceiveChannel<Int> = produce {
    repeat(3){ num -> send(num + 1) }
}

private fun CoroutineScope.square(numbers:ReceiveChannel<Int>) = produce {
    for(num in numbers) send(num * num)
}

suspend fun CoroutineScope.serveOrders(
    orders: ReceiveChannel<Order>,
    baristaName: String
):ReceiveChannel<CoffeResult> = produce {
    for(order in orders){
        val coffee = prepareCoffee(order.type)
        send(
            CoffeeResult(
                coffee = coffee,
                customer = order.customer,
                varistaName = baristaName
            )
        )
    }
}