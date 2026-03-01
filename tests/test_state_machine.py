from coc_helper import AssistantStateMachine, Scene


def test_full_flow_reaches_done():
    machine = AssistantStateMachine()

    steps = [machine.next() for _ in range(20)]

    assert Scene.DONE in steps
    assert machine.state == Scene.DONE


def test_reset_to_idle():
    machine = AssistantStateMachine()
    machine.start()
    machine.next()

    machine.reset()

    assert machine.state == Scene.IDLE
    assert machine.context.has_army is False
