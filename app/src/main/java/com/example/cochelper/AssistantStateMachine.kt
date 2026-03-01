package com.example.cochelper

enum class Scene(val title: String) {
    IDLE("待机"),
    HOME("主城"),
    FINDING_FARM_TARGET("搜索打鱼目标"),
    ATTACKING("战斗中"),
    RETURN_HOME("回城结算"),
    UPGRADE_BUILDING("升级建筑"),
    UPGRADE_TROOPS("升级兵种"),
    DONE("流程完成")
}

data class Context(
    var hasArmy: Boolean = false,
    var hasEnoughResource: Boolean = false,
    var battleFinished: Boolean = false,
    var buildingUpgradeDone: Boolean = false,
    var troopUpgradeDone: Boolean = false
)

class AssistantStateMachine {
    var context: Context = Context()
        private set

    var state: Scene = Scene.IDLE
        private set

    fun start(): Scene {
        state = Scene.HOME
        return state
    }

    fun reset(): Scene {
        context = Context()
        state = Scene.IDLE
        return state
    }

    fun next(): Scene {
        state = when (state) {
            Scene.IDLE -> start()
            Scene.HOME -> fromHome()
            Scene.FINDING_FARM_TARGET -> fromFinding()
            Scene.ATTACKING -> fromAttacking()
            Scene.RETURN_HOME -> Scene.UPGRADE_BUILDING
            Scene.UPGRADE_BUILDING -> fromUpgradeBuilding()
            Scene.UPGRADE_TROOPS -> fromUpgradeTroops()
            Scene.DONE -> Scene.DONE
        }
        return state
    }

    private fun fromHome(): Scene {
        if (!context.hasArmy) {
            context.hasArmy = true
            return Scene.HOME
        }
        return Scene.FINDING_FARM_TARGET
    }

    private fun fromFinding(): Scene {
        if (!context.hasEnoughResource) {
            context.hasEnoughResource = true
            return Scene.FINDING_FARM_TARGET
        }
        return Scene.ATTACKING
    }

    private fun fromAttacking(): Scene {
        if (!context.battleFinished) {
            context.battleFinished = true
            return Scene.ATTACKING
        }
        return Scene.RETURN_HOME
    }

    private fun fromUpgradeBuilding(): Scene {
        if (!context.buildingUpgradeDone) {
            context.buildingUpgradeDone = true
            return Scene.UPGRADE_BUILDING
        }
        return Scene.UPGRADE_TROOPS
    }

    private fun fromUpgradeTroops(): Scene {
        if (!context.troopUpgradeDone) {
            context.troopUpgradeDone = true
            return Scene.UPGRADE_TROOPS
        }
        return Scene.DONE
    }
}
