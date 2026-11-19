# 问题跟踪：GitHub

本仓库的问题和规格记录在 GitHub Issues 中。所有相关操作均使用 `gh` CLI。

## 约定

- **创建问题**：`gh issue create --title "..." --body "..."`。多行正文可使用当前 Shell 对应的 here-doc 或 here-string。
- **读取问题**：`gh issue view <number> --json number,title,body,labels,comments`。
- **列出问题**：`gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`，并按需添加 `--label` 和 `--state` 过滤条件。
- **将问题设为父问题的子问题**：`gh issue create --parent <parent> ...`，或随后执行 `gh issue edit <parent> --add-sub-issue <child>`（需要 `gh` 2.94+）。旧版本可执行 `gh api --method POST repos/<owner>/<repo>/issues/<parent>/sub_issues -F sub_issue_id=<child-db-id>`，其中 ID 是子问题的数据库 ID，规则见下面的 **依赖关系**。如果无法使用子问题功能，请在子问题正文顶部添加 `Part of #<parent>`。
- **依赖关系**：优先使用 GitHub 原生问题依赖，这是可追踪且会在界面中展示的规范表示。使用 `gh issue create --blocked-by <n>,<n> ...` 或 `gh issue edit <child> --add-blocked-by <blocker>` 添加依赖（需要 `gh` 2.94+）。旧版本可执行 `gh api --method POST repos/<owner>/<repo>/issues/<child>/dependencies/blocked_by -F issue_id=<blocker-db-id>`，其中 `<blocker-db-id>` 是阻塞问题的数字数据库 ID，可通过 `gh api repos/<owner>/<repo>/issues/<n> --jq .id` 获取，而不是 `#number` 或 `node_id`。如果依赖功能不可用，请在子问题正文顶部添加 `Blocked by: #<n>, #<n>`。所有阻塞问题关闭后，该问题才算解除阻塞。
- **统计未关闭的阻塞问题**：`gh api repos/<owner>/<repo>/issues/<n> --jq .issue_dependencies_summary.blocked_by` 只统计仍处于开启状态的阻塞问题，返回 `0` 表示可以开始处理。`gh issue view <n> --json blockedBy` 也会包含已关闭的阻塞问题，因此需要过滤：`--jq '[.blockedBy.nodes[] | select(.state == "OPEN")] | length'`。
- **评论问题**：`gh issue comment <number> --body "..."`。
- **添加或移除标签**：`gh issue edit <number> --add-label "..."` 或 `gh issue edit <number> --remove-label "..."`。
- **关闭问题**：先用 `gh issue comment <number> --body "..."` 发布说明，再执行 `gh issue close <number>`。对于已经关闭的问题，`gh issue close --comment` 会丢弃评论但仍返回成功。

在仓库克隆目录内运行时，`gh` 会根据 `git remote -v` 自动推断仓库。

只能向用户自己的仓库创建问题。除非用户拥有 `mattpocock/skills`，否则不要向该仓库创建问题。在 fork 中，`gh` 可能默认指向上游父仓库，因此会话中第一次执行 `gh issue create` 前，先运行 `gh repo view --json nameWithOwner` 检查目标仓库；如果不是用户自己的仓库，先向用户确认。

## Pull Request 作为分诊入口

**将 PR 作为请求入口：否。** _如果本仓库将外部 PR 视为功能请求，请改为 `yes`；`/triage` 会读取此设置。_

设置为 `yes` 后，PR 使用与 Issue 相同的标签和状态，并通过对应的 `gh pr` 命令处理：

- **读取 PR**：`gh pr view <number>`，然后运行 `gh pr view <number> --comments`（非 TTY 环境下，`--comments` 只输出评论而不输出正文），并使用 `gh pr diff <number>` 查看差异。
- **列出用于分诊的外部 PR**：`gh api --paginate 'repos/{owner}/{repo}/pulls?state=open' --jq '.[] | select(.author_association | IN("OWNER","MEMBER","COLLABORATOR") | not) | {number, title, author: .user.login, author_association, labels: [.labels[].name]}'`。
- **评论、添加标签、关闭**：`gh pr comment`、`gh pr edit --add-label`/`--remove-label`、`gh pr close`。

GitHub 的 Issue 和 PR 共用编号空间，因此单独的 `#42` 可能是 Issue，也可能是 PR。先运行 `gh pr view 42`，失败后再运行 `gh issue view 42`。

## 当技能要求“发布到问题跟踪器”时

创建一个 GitHub Issue。

## 当技能要求“获取相关工单”时

按照上面的 **读取问题** 操作执行。

## Wayfinder 操作

供 `/wayfinder` 使用。**地图**是一个父 Issue，**子 Issue**作为具体工单。

- **地图**：创建一个带 `wayfinder:map` 标签的 Issue，正文包含 Notes、Decisions-so-far 和 Fog。命令：`gh issue create --label wayfinder:map`。
- **子工单**：作为 GitHub 子问题关联到地图（见 **将问题设为父问题的子问题**）。如果无法使用子问题功能，将子问题加入地图正文的任务列表，并在子问题正文顶部添加 `Part of #<map>`。标签使用 `wayfinder:<type>`，其中 `type` 为 `research`、`prototype`、`grilling` 或 `task`。认领后，将工单分配给当前开发者。
- **依赖关系**：见 **约定** 中的 **依赖关系**。
- **查找可执行工单**：列出地图的未关闭子问题（`gh issue list --state open`，范围限定为地图的子问题或任务列表），排除存在未关闭阻塞问题（见 **统计未关闭的阻塞问题**，或正文中的 `Blocked by` 行）或已有负责人的工单；按地图中的顺序选择第一个。
- **认领**：`gh issue edit <n> --add-assignee @me`，这是该会话的第一次写操作。
- **解决**：`gh issue comment <n> --body "<answer>"`，然后执行 `gh issue close <n>`，最后将上下文指针（摘要和链接）追加到地图的 Decisions-so-far。
