# Android Services
Comparison of using plain Android Service v IntentService

A little contrived.

Aim is to have a service that continues to performs actions on a timed basis for a specified length of time.

While IntentService is generally preferred for simplicity, in this case, there is the need to stop the IntentService from ending.

Using a plain service is more work (slightly) but does not require anyblocking synchronisation primitives.

Much more important though, is the threading of the two implementations. ie the use of the Main thread, or the use of a worker thread. While the IntentService creates it's own worker thread to run onHandleIntent, that thread must be blocked to stop the service from finishing, so the timed operations must either be run on the main thread - or else another worker thread is required. 


