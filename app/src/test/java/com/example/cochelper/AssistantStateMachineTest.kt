package com.example.cochelper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantStateMachineTest {
    @Test
    fun fullFlowReachesDone() {
        val machine = AssistantStateMachine()
        repeat(20) { machine.next() }
        assertEquals(Scene.DONE, machine.state)
    }

    @Test
    fun resetBackToIdle() {
        val machine = AssistantStateMachine()
        machine.start()
        machine.next()

        machine.reset()

        assertEquals(Scene.IDLE, machine.state)
        assertTrue(!machine.context.hasArmy)
    }
}
