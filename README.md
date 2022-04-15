# TeeLogin (Fabric Mod)

## 说明

这是一个 Fabric 下的模组，用于管理服务器登录，仅服务端安装。  
使用/login <password> 登入游戏，未登录则为旁观者模式。  
使用/login <password> register 注册一个密码，需要输入两次。  

## 如何管理？

模组会使用 `./config/TeeLogin` 作为文件存放根，其中
  * `location.json` 记录玩家最后一次成功登入之后的下线地点
  * `password.json` 以明文记录玩家的密码，格式为 "{player-uuid}-{player-name}": "<password>"
如果需要修改其中任何文件，需要在关闭服务端卸载模组之后操作，否则修改内容将被覆盖。
