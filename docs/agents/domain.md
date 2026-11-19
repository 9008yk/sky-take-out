# 领域文档

本文件规定工程技能在探索代码库时应如何使用本仓库的领域文档。

## 探索前先阅读

- 仓库根目录的 **`GLOSSARY.md`**；或
- 如果存在仓库根目录的 **`GLOSSARY-MAP.md`**，它会指向每个上下文各自的 `GLOSSARY.md`。阅读与当前任务相关的每个术语表。
- **`docs/adr/`**：阅读涉及当前工作区域的 ADR。在多上下文仓库中，还要检查 `src/<context>/docs/adr/` 中与上下文相关的决策。

如果这些文件或目录不存在，**安静地继续**。不要报告其缺失，也不要主动建议提前创建。`/domain-modeling` 技能（可通过 `/grill-with-docs` 和 `/improve-codebase-architecture` 使用）会在术语或决策真正确定后按需创建它们。

## 文件结构

单上下文仓库（大多数仓库）：

```text
/
├── GLOSSARY.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文仓库（根目录存在 `GLOSSARY-MAP.md`）：

```text
/
├── GLOSSARY-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── GLOSSARY.md
    │   └── docs/adr/                  ← 上下文相关决策
    └── billing/
        ├── GLOSSARY.md
        └── docs/adr/
```

## 使用术语表中的词汇

当输出中需要命名领域概念时（例如 Issue 标题、重构提案、假设或测试名称），使用 `GLOSSARY.md` 中定义的术语。不要改用术语表明确避免的同义词。

如果需要使用的概念尚未进入术语表，这表示：要么你正在发明项目并未使用的语言，应当重新考虑；要么确实存在术语缺口，应记录并交给 `/domain-modeling` 处理。

## 标记 ADR 冲突

如果输出与已有 ADR 冲突，应明确提示，而不是静默覆盖：

> _与 ADR-0007（事件溯源订单）冲突，但值得重新讨论，因为……_
