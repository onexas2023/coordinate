var Xterm = axeswgt.xterm.Xterm = zk.$extends(zul.wgt.Div, {

	$init : function() {
		this.$supers('$init', arguments);
	},
	$define : {
		websocketUri : function() {
			if(this._terminal){				
				this.reconnect();
			}
		}
	},
	bind_ : function() {
		this.$supers(Xterm, 'bind_', arguments);

		var terminal = new Terminal();
		terminal.open(this.$n());
		terminal.fit();
		this._terminal = terminal;

		this.reconnect();
		
		zWatch.listen({
			onSize : this
		});
	},
	unbind_ : function() {
		if (this._terminal) {
			this._terminal.destroy();
			delete this._terminal;
		}
		if (this._socket) {
			this._socket.close();
			delete this._socket;
		}
		zWatch.unlisten({
			onSize : this
		});
		this.$supers(Xterm, 'unbind_', arguments);
	},
	reconnect : function() {
		if (this._socket) {
			this._socket.close();
			delete this._socket;
		}
		this.clear();
		if (this._websocketUri) {
			this.write("Connect...");
			var fn = this.proxy(this._reconnect0);
			setTimeout(fn,50);
		}else{
			this.write("No connection.");
		}
	},
	_reconnect0 : function() {
		try {
			var thix = this;
			var socket = new WebSocket(this._websocketUri);
			socket.onerror = function (evt) {
				console.log(evt);
				if(thix._terminal){
					thix.newline();
					thix.write("Connection error.");
				}
			}
			socket.onclose = function (evt){
				if(thix._terminal){
					thix.newline();
					thix.write("Connection closed.");
				}
			}
			this._socket = socket;
			this.write('Connected.');
			this.newline();
			this._terminal.attach(socket);
		} catch (e) {
			console.log(e.message);
			thix.newline();
			this.write('Connection fail.');
		}
	},
	newline : function(){
		this._terminal.write('\n\r');
	},
	clear : function(){
		this._terminal.write('\x1b[H\x1b[2J');
	},
	write : function(str){
		this._terminal.write(str);
	},
	getZclass : function() {
		return this._zclass != null ? this._zclass : "z-xterm";
	},
	getTerminal : function() {
		return this._terminal;
	},
	getSocket : function() {
		return this._socket;
	},
	onSize : function() {
		if (this._terminal) {
			this._terminal.fit();
		}
	}
});
