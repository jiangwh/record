###Tree
## 比较怀念win下面tree的命令，tree命令可以生成目录树。使用mac系统可以使用find命令替代tree。可以在.bash_profile文件增加别名信息如下： 
```
   alias tree="find . -print | sed -e 's;[^/]*/;|____;g;s;____|; |;g'"
```

 * tree命令效果如下： 
```

	jiangwh:store jiangwh$ tree
	.
	|____checkpoint
	|____config
	| |____consumerFilter.json
	| |____consumerFilter.json.bak
	| |____consumerOffset.json
	| |____consumerOffset.json.bak
	| |____delayOffset.json
	| |____delayOffset.json.bak	
