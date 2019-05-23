# stateless-flow
A Stateless State Machine

Maven
=====
```
```

Introduction
============
Create light-weight workflow graph using state-machine

Supports different java-types for states and operations (numbers, strings, enums, etc.)

Example : Defining the states and transitions.
```java
StateMachine<String, String> stateMachine = StateMachine
                .builder()
                .states(states)
                .operations(operations)
                .transition("from", "to", "action")
                .transition("abc", "xyz", "approved")
                .transition("abc", "efg", "reject",(from,to,operation)->{
                    log.error("this was rejected");
                })
                .build();

```

```java
Usage:-
Boolean hasNext = stateMachine.hasNext("from", "to");
String next = stateMachine.next("from", "to");
String nextState = stateMachine.moveToNext("from", "to");
```