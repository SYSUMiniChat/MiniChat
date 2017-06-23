## 本地数据库的接口

### userDB

* boolean insert2Table 插入
  + 参数
    - id 用户id(唯一)
    - nickname 昵称
    - sex 性别
    - city 城市
    - signature 个性签名
    - avatar 头像
    - finalDate 最后更新时间
  + 返回值
    - true or false
* Cursor findOneByNumber 根据id查找用户信息
  + 参数
    - id 用户id
  + 返回值
    - 查找到的数据
* void updateInfo 更新用户某一项数据
  + 参数
    - id 用户id
    - col 列名(具体值见insert参数)
    - value 新的值
    - date 更新时间

### recentListDB

* boolean insertOne 插入
  + 参数
    - sender 当前用户id
    - receiver 对面的id
  + 返回值
    - true or false
* Cursor getItems 获取当前用户的最近联系人列表
  + 参数
    - sender 当前用户id
  + 返回值
    - 当前用户的最近联系人列表

### recordDB

* boolean insertOne  插入一条聊天记录
  + 参数
    - type 消息类型 0表示当前用户发送，1表示接收
    - sender 即当前用户id
    - receiver 对面用户id
    - content 聊天内容
  + 返回值
* Cursor getItems  获取当前用户与某用户的所有聊天记录
  + 参数
    - sender 当前用户id
    - receiver 对面用户id
  + 返回值 
* Cursor getLastItem  获取当前用户与某用户的最后一条聊天记录
  + 参数
    - sender 当前用户id
    - receiver  对面用户id
  + 返回值
    - 最后一条记录
