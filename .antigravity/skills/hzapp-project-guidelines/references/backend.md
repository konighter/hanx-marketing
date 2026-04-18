# Java 后端开发规范 (hzapp-erplus)

## 1. 命名与后缀

所有对象命名必须严格遵守以下规则：

- **DO (Data Object)**: `Erp*DO`（对应数据库表）
- **RespVO**: `Erp*RespVO`（返回给前端的对象）
- **ReqVO**:
    - 保存/创建/更新: `Erp*SaveReqVO`
    - 分页查询: `Erp*PageReqVO`

## 2. 架构模式：Controller vs Service

### Controller (瘦层)
- 职责：请求映射、权限校验 (@PreAuthorize)、基本参数校验 (@Validated)。
- **严禁**：在 Controller 中直接聚合多表数据、处理复杂的逻辑判断。
- 示例：
  ```java
  public CommonResult<PageResult<ErpItemRespVO>> getPage(@Valid ErpItemPageReqVO pageReqVO) {
      return success(itemService.getItemPage(pageReqVO));
  }
  ```

### Service (胖层)
- 职责：核心业务逻辑、多表关联查询、元数据组装（如通过 ID 查名称）。
- 批次优化：在组装数据时，必须使用批次查询（Batch Get/Map）避免 N+1 SQL 问题。
- 示例：
  ```java
  public PageResult<ErpItemRespVO> getItemPage(ErpItemPageReqVO pageReqVO) {
      // 1. 分页查询主表
      PageResult<ErpItemDO> pageResult = itemMapper.selectPage(pageReqVO);
      // 2. 提取 ID 批次查询关联表并转换为 Map
      Map<Long, String> categoryMap = categoryService.getCategoryNameMap(convertList(pageResult.getList(), ErpItemDO::getCategoryId));
      // 3. 组装 VO
      return new PageResult<>(convertList(pageResult.getList(), item -> {
          ErpItemRespVO vo = BeanUtils.toBean(item, ErpItemRespVO.class);
          vo.setCategoryName(categoryMap.get(item.getCategoryId()));
          return vo;
      }), pageResult.getTotal());
  }
  ```

## 3. 代码整洁度

- **避免全限定类名**：禁止在代码中使用 `java.util.List` 等全名，统统使用 `import`。
- **消除魔法数字**：所有的业务状态、类型标识必须定义在 `enums` 模块中并引用。
- **Map 工具类**：优先使用项目封装的 `MapUtils.findAndThen` 进行安全赋。
