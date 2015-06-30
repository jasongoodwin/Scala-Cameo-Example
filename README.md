Scala Cameo Example
===================

This demonstrates a more efficient alternative than ask.
This is an optimization and introduces complexity to code when compared to ask.
However, it reduces the memory footprint:

Each ask (actor ? message) has an overhead:
- One future is created for each ask
- One temporary actor is created for each ask
- One timeout is created on each future

The upside of ask is that it is simpler in code.
The downsides are that multiple timeouts can appear in logs without a clear indicator of exactly what failed where and why.
Using cameo cuts the memory overhead down to one future and one temporary actor for any group of responses you need to get.
In the example we only care about one type of response, but it's more idiomatic if you're trying to get responses from multiple services.
Another big benefit is that all of the timeouts in the logs dissapear and are replaced by your own log event that can describe exactly what you got, what you didn't get, and can handle the partial completion more declaratively.

Cameo can actually be simpler than asks when you're trying to compose together requests from multiple services when you can tolerate partial failures - it's easier to put all of the code in one place than it is trying to compose together multiple futures. 

But what about compared to only futures? You reduce the memory overhead if you use futures instead of actors, but again the complexity of trying to compose multiple futures can be dealt with by using the cameo pattern. Whenever you are trying to get data from multiple datasources, you should evaluate if this pattern is a good fit relative to another solution. Is it simpler? If so, then it's an easy win. If you're trying for performance, it's a less obvious decision and you may need to start to measure to determine if there is a benefit.

"Tell Don't Ask" is often a good approach when you're looking at how to compose actors together. This pattern shows us how we can respond asynchronously without using ask.

