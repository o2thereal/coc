from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from typing import Callable, Dict


class Scene(str, Enum):
    IDLE = "待机"
    HOME = "主城"
    FINDING_FARM_TARGET = "搜索打鱼目标"
    ATTACKING = "战斗中"
    RETURN_HOME = "回城结算"
    UPGRADE_BUILDING = "升级建筑"
    UPGRADE_TROOPS = "升级兵种"
    DONE = "流程完成"


@dataclass
class Context:
    has_army: bool = False
    has_enough_resource: bool = False
    battle_finished: bool = False
    building_upgrade_done: bool = False
    troop_upgrade_done: bool = False


@dataclass
class AssistantStateMachine:
    context: Context = field(default_factory=Context)
    state: Scene = Scene.IDLE

    def start(self) -> Scene:
        self.state = Scene.HOME
        return self.state

    def reset(self) -> Scene:
        self.context = Context()
        self.state = Scene.IDLE
        return self.state

    def next(self) -> Scene:
        transition_map: Dict[Scene, Callable[[], Scene]] = {
            Scene.IDLE: self.start,
            Scene.HOME: self._from_home,
            Scene.FINDING_FARM_TARGET: self._from_finding,
            Scene.ATTACKING: self._from_attacking,
            Scene.RETURN_HOME: self._from_return,
            Scene.UPGRADE_BUILDING: self._from_upgrade_building,
            Scene.UPGRADE_TROOPS: self._from_upgrade_troops,
            Scene.DONE: lambda: Scene.DONE,
        }
        self.state = transition_map[self.state]()
        return self.state

    def _from_home(self) -> Scene:
        if not self.context.has_army:
            self.context.has_army = True
            return Scene.HOME
        return Scene.FINDING_FARM_TARGET

    def _from_finding(self) -> Scene:
        if not self.context.has_enough_resource:
            self.context.has_enough_resource = True
            return Scene.FINDING_FARM_TARGET
        return Scene.ATTACKING

    def _from_attacking(self) -> Scene:
        if not self.context.battle_finished:
            self.context.battle_finished = True
            return Scene.ATTACKING
        return Scene.RETURN_HOME

    def _from_return(self) -> Scene:
        return Scene.UPGRADE_BUILDING

    def _from_upgrade_building(self) -> Scene:
        if not self.context.building_upgrade_done:
            self.context.building_upgrade_done = True
            return Scene.UPGRADE_BUILDING
        return Scene.UPGRADE_TROOPS

    def _from_upgrade_troops(self) -> Scene:
        if not self.context.troop_upgrade_done:
            self.context.troop_upgrade_done = True
            return Scene.UPGRADE_TROOPS
        return Scene.DONE
