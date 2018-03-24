if (window.ACTIVER === undefined) {
    window.ACTIVER = {};
}

window.ACTIVER.Global = {
    stompClient: null,
    /* This structure is intended for messaging */
    message: {
        to: null,
        message: null,
        type: null
    },
    init: function() {
        this.connect();
    },
    submit: function(data){
        this.stompClient.send("/actions",{},JSON.stringify(data));
    },
    handlers:{},
    handlersWithNamespace: {},

    /**
     * Функция устанавливает триггеры на сообщения, приходящие через WebSocket
     *
     * @param type тип сообщения
     * @param f Функция содержащая код обработки
     */
    on: function(type,f) {
        if (typeof type === "string" && typeof f === "function") {
            if (type.indexOf(".") !== -1) {
                this.handlersWithNamespace[type] = f;
                return;
            }
            if (this.handlers.hasOwnProperty(type)) {
                if (this.handlers[type] instanceof Array) {
                    this.handlers[type].push(f);
                } else if (this.handlers[type] instanceof Function)  {
                    this.handlers[type] = [this.handlers[type],f];
                } else {
                    this.handlers[type] = [f];
                }
            } else {
                this.handlers[type] = [f];
            }
        }
    },

    connect: function() {
        var that = this;
        var socket = new SockJS(window.ACTIVER.context_path + '/global');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, function (frame) {
            console.log('%c' + frame, 'background: #222; color: #bada55');
            that.stompClient.subscribe('/user/global2', function (greeting) {
                var result = JSON.parse(greeting.body);
                for (var j in that.handlersWithNamespace) {
                    if (that.handlersWithNamespace.hasOwnProperty(j)) {
                        var event = j.split(".");
                        if (event[0] === result.type) {
                            that.handlersWithNamespace[j](result);
                        }
                    }
                }

                if (that.handlers.hasOwnProperty(result.type)) {
                    if (that.handlers[result.type] instanceof Array) {
                        for (var index in that.handlers[result.type]) {
                            if (that.handlers[result.type].hasOwnProperty(index)) {
                                that.handlers[result.type][index](result);
                            }
                        }
                    } else {
                        that.handlers[result.type](result);
                    }
                } else {
                    if (that.onPRIVATE_MESSAGE !== null) {
                        that.onPRIVATE_MESSAGE(result);
                    }
                }
            });
        });
    },
    onPRIVATE_MESSAGE:null
};