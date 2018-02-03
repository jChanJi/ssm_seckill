//存放组主要交互逻辑js代码
//JavaScript模块化
var Seckill = {
    //封装秒杀相关ajax的URL
    URL: {
        now: function () {
            return "/seckill/time/now";
        },
        exposer:function (seckillId) {
            return "/seckill/"+seckillId+"/exposer";
        },
        execution:function (seckillId, md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    vaildataPhone: function (phone) {
        /* if (phone && phone.length === 11 && !isNaN(phone)) {
             return true;
         } else {
             return false;
         }*/
        return !!(phone && phone.length === 11 && !isNaN(phone))
    },

    handleSeckillKill: function (seckillId,node) {
        //获取秒杀地址，控制实现逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>'); //秒杀按钮

        $.post(Seckill.URL.exposer(seckillId),{},function (result) {
        //在回调函数中执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = Seckill.URL.execution(seckillId,md5);
                     console.log('killUrl:'+killUrl);
                     //只进行依次的秒杀操作，防止用户点击多次全部发送给服务器，导致服务器的负担过重
                     $('#killBtn').one('click',function () {
                         //执行秒杀请求的操作
                         //1：先禁用按钮
                         $(this).addClass('disabled');
                         //2：发送秒杀请求执行秒杀
                         $.post(killUrl,{},function (result) {
                             //console.log(result)
                             if(result && result['success']){
                                // console.log(result)
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //3：显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>')
                             }
                         });
                     });
                     node.show();
                }else{
                    //未开始秒杀
                    //1、每个人的机器在运行的时候时间长的时候时会有偏差的，所以需要再次的获取时间
                    var now  =exposer['now'];
                    var start= exposer['start'];
                    var end = exposer['end'];
                    //再次进入计时判断阶段
                    Seckill.countdown(seckillId,now,start,end);
                }
                 
            }else{
                    console.log('result:'+result)
            }
        });
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //获取秒杀地址，控制实现逻辑，执行秒杀
                Seckill.handleSeckillKill(seckillId,seckillBox)
            })
        } else {
            //秒杀开始
            Seckill.handleSeckillKill(seckillId,seckillBox)
        }

    },
    //详情页秒杀逻辑
    detail: {
        init: function (params) {
            //用户手机验证和登陆，计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号
            //手机号为空
            var result = Seckill.vaildataPhone(killPhone);
            //   console.log(result);
            if (!result) {
                //显示弹出层
                $('#killPhoneModal').modal({
                    show: true, // 显示弹出层
                    backdrop: 'static',  // 静止位置关闭
                    keyboard: false// 关闭键盘事件
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (Seckill.vaildataPhone(inputPhone)) {
                        //电话写入cookie
                        //expires 有效期7天，路径为seckill下，不给全路径，因为cookie用不到的时候也会传递给后端，会影响效率
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        //手机号验证不通过，这里的返回可以设置为字典
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //已经登陆，计时操作
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(Seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data']
                    //时间判断
                    Seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }

            });
        }
    }
}