from __future__ import annotations

import tkinter as tk
from tkinter import ttk

from .state_machine import AssistantStateMachine, Scene


class FloatingWindow:
    def __init__(self, root: tk.Tk, on_open_dashboard):
        self.window = tk.Toplevel(root)
        self.window.title("COC 辅助悬浮窗")
        self.window.geometry("220x120+60+60")
        self.window.attributes("-topmost", True)

        ttk.Label(self.window, text="COC 辅助", font=("Arial", 12, "bold")).pack(pady=8)
        ttk.Button(self.window, text="打开控制台", command=on_open_dashboard).pack(fill="x", padx=10, pady=4)
        ttk.Button(self.window, text="最小化", command=self.window.iconify).pack(fill="x", padx=10)


class Dashboard:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.machine = AssistantStateMachine()

        self.window = tk.Toplevel(root)
        self.window.title("COC 基本流程页面")
        self.window.geometry("460x360+320+60")

        self.current_state_var = tk.StringVar(value=f"当前状态：{self.machine.state.value}")
        self.status_label = ttk.Label(self.window, textvariable=self.current_state_var, font=("Arial", 12, "bold"))
        self.status_label.pack(pady=10)

        self.progress = tk.Text(self.window, height=12, width=54)
        self.progress.pack(padx=10, pady=8)

        controls = ttk.Frame(self.window)
        controls.pack(fill="x", padx=10, pady=8)

        ttk.Button(controls, text="开始流程", command=self.start_flow).pack(side="left", padx=4)
        ttk.Button(controls, text="下一步", command=self.next_step).pack(side="left", padx=4)
        ttk.Button(controls, text="自动跑完", command=self.run_all).pack(side="left", padx=4)
        ttk.Button(controls, text="重置", command=self.reset).pack(side="left", padx=4)

        self._log("已初始化：等待开始")

    def start_flow(self):
        self.machine.start()
        self._refresh()
        self._log("进入主城，检查训练与资源")

    def next_step(self):
        state = self.machine.next()
        self._refresh()
        self._log(self._scene_action(state))

    def run_all(self):
        safety = 0
        while self.machine.state != Scene.DONE and safety < 50:
            self.next_step()
            self.window.update_idletasks()
            safety += 1

    def reset(self):
        self.machine.reset()
        self._refresh()
        self._log("状态已重置")

    def _refresh(self):
        self.current_state_var.set(f"当前状态：{self.machine.state.value}")

    def _log(self, text: str):
        self.progress.insert("end", f"- {text}\n")
        self.progress.see("end")

    @staticmethod
    def _scene_action(state) -> str:
        mapping = {
            "主城": "检查兵营与法术，准备打鱼",
            "搜索打鱼目标": "通过状态机识别到搜索界面，筛选可打资源村",
            "战斗中": "执行下兵顺序并监测战斗结算",
            "回城结算": "战斗结束回到主城，统计资源",
            "升级建筑": "优先升级采集器/仓库等建筑",
            "升级兵种": "进入实验室，执行兵种升级",
            "流程完成": "打鱼+升级流程已完成一轮",
            "待机": "处于待机状态",
        }
        return mapping.get(state.value, f"状态切换：{state.value}")


def launch_app():
    root = tk.Tk()
    root.withdraw()

    dashboard_holder = {"dashboard": None}

    def open_dashboard():
        if dashboard_holder["dashboard"] is None or not dashboard_holder["dashboard"].window.winfo_exists():
            dashboard_holder["dashboard"] = Dashboard(root)

    FloatingWindow(root, open_dashboard)
    root.mainloop()
