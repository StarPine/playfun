package com.dl.playfun.data.source.http.service;

import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseListDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.AccostEntity;
import com.dl.playfun.entity.AlbumPhotoEntity;
import com.dl.playfun.entity.AllConfigEntity;
import com.dl.playfun.entity.ApplyMessageEntity;
import com.dl.playfun.entity.BaseUserBeanEntity;
import com.dl.playfun.entity.BlackEntity;
import com.dl.playfun.entity.BoradCastMessageEntity;
import com.dl.playfun.entity.BroadcastEntity;
import com.dl.playfun.entity.BroadcastListEntity;
import com.dl.playfun.entity.BrowseNumberEntity;
import com.dl.playfun.entity.BubbleEntity;
import com.dl.playfun.entity.CallingInfoEntity;
import com.dl.playfun.entity.CallingInviteInfo;
import com.dl.playfun.entity.CallingStatusEntity;
import com.dl.playfun.entity.CashWalletEntity;
import com.dl.playfun.entity.ChatDetailCoinEntity;
import com.dl.playfun.entity.ChatRedPackageEntity;
import com.dl.playfun.entity.CoinExchangeBoxInfo;
import com.dl.playfun.entity.CoinWalletEntity;
import com.dl.playfun.entity.CommentMessageEntity;
import com.dl.playfun.entity.ConfigItemEntity;
import com.dl.playfun.entity.CreateOrderEntity;
import com.dl.playfun.entity.EvaluateEntity;
import com.dl.playfun.entity.EvaluateMessageEntity;
import com.dl.playfun.entity.FaceVerifyResultEntity;
import com.dl.playfun.entity.GameCoinBuy;
import com.dl.playfun.entity.GameCoinWalletEntity;
import com.dl.playfun.entity.GamePhotoAlbumEntity;
import com.dl.playfun.entity.GiftBagEntity;
import com.dl.playfun.entity.GiveMessageEntity;
import com.dl.playfun.entity.GoodsEntity;
import com.dl.playfun.entity.GoogleNearPoiBean;
import com.dl.playfun.entity.GooglePoiBean;
import com.dl.playfun.entity.IMTransUserEntity;
import com.dl.playfun.entity.IsChatEntity;
import com.dl.playfun.entity.LevelApiEntity;
import com.dl.playfun.entity.LevelPageInfoEntity;
import com.dl.playfun.entity.MessageGroupEntity;
import com.dl.playfun.entity.MessageRuleEntity;
import com.dl.playfun.entity.NewsEntity;
import com.dl.playfun.entity.OccupationConfigItemEntity;
import com.dl.playfun.entity.ParkItemEntity;
import com.dl.playfun.entity.PhotoAlbumEntity;
import com.dl.playfun.entity.PriceConfigEntity;
import com.dl.playfun.entity.PrivacyEntity;
import com.dl.playfun.entity.ProfitMessageEntity;
import com.dl.playfun.entity.PushSettingEntity;
import com.dl.playfun.entity.RadioTwoFilterItemEntity;
import com.dl.playfun.entity.SignMessageEntity;
import com.dl.playfun.entity.SoundEntity;
import com.dl.playfun.entity.StatusEntity;
import com.dl.playfun.entity.SwiftMessageEntity;
import com.dl.playfun.entity.SwitchesEntity;
import com.dl.playfun.entity.SystemMessageEntity;
import com.dl.playfun.entity.TagEntity;
import com.dl.playfun.entity.TaskAdEntity;
import com.dl.playfun.entity.TokenEntity;
import com.dl.playfun.entity.TopicalListEntity;
import com.dl.playfun.entity.TraceEntity;
import com.dl.playfun.entity.UnReadMessageNumEntity;
import com.dl.playfun.entity.UserCoinItemEntity;
import com.dl.playfun.entity.UserConnMicStatusEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.entity.UserDetailEntity;
import com.dl.playfun.entity.UserInfoEntity;
import com.dl.playfun.entity.UserProfitPageEntity;
import com.dl.playfun.entity.UserRemarkEntity;
import com.dl.playfun.entity.VersionEntity;
import com.dl.playfun.entity.VipPackageItemEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author goldze
 * @date 2017/6/15
 */

public interface ApiService {


    /**
     * @return io.reactivex.Observable<com.dl.play.chat.data.source.http.response.BaseDataResponse < com.dl.play.chat.entity.LevelApiEntity>>
     * @Desc TODO(主播调价)
     * @author 彭石林
     * @parame [requestBody]
     * @Date 2022/6/22
     */
    @POST("calling/userLevel/adjustPrice")
    @Headers("Content-Type: application/json")
    Observable<BaseDataResponse<LevelApiEntity>> adjustLevelPrice(@Body RequestBody requestBody);

    /**
     * @return io.reactivex.Observable<com.dl.play.chat.data.source.http.response.BaseDataResponse < com.dl.play.chat.entity.UserLevelPageInfoEntity>>
     * @Desc TODO(用户等级功能页面)
     * @author 彭石林
     * @parame []
     * @Date 2022/6/21
     */
    @GET("/calling/userLevel/getUserLevelPageInfo")
    Observable<BaseDataResponse<LevelPageInfoEntity>> getUserLevelPageInfo();

    /**
    * @Desc TODO(根据邮箱发送验证码)
    * @author 彭石林
    * @parame []
    * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
    * @Date 2022/5/17
    */
    @FormUrlEncoded
    @POST("api/email/send")
    Observable<BaseResponse> sendEmailCode(@Field("email")String email);

    /**
    * @Desc TODO(绑定用户邮箱)
    * @author 彭石林
    * @parame [email, code, pass, type]
    * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse<com.dl.playfun.entity.UserDataEntity>>
    * @Date 2022/5/17
    */
    @FormUrlEncoded
    @POST("api/email/bind")
    Observable<BaseResponse> bindUserEmail(
            @Field("email")String email, //邮箱账号
            @Field("code") String code, //验证码
            @Field("pass") String pass, //账户密码
            @Field("type") Integer type //1绑定邮箱(邮箱/验证码) 2设置密码(两次密码) 3绑定邮箱(邮箱/验证码/两次密码) 4修改密码(验证码/两次密码)
    );
    
    /**
    * @Desc TODO(邮箱登录)
    * @author 彭石林
    * @parame [email, code, type]
    * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
    * @Date 2022/5/17
    */
    @FormUrlEncoded
    @POST("api/email/login")
    Observable<BaseDataResponse<UserDataEntity>> loginEmail(
            @Field("email")String email, //邮箱账号
            @Field("code") String code, //验证码/密码
            @Field("type") Integer type //1验证码登陆 2密码登陆
    );

    /**
     * 获取通话状态
     * 需要两个人都已进入房间，分两种情况：1、已解散房间；2、未解散房间。房间已解散状态下部分字段返回0
     *
     * @param roomId
     * @return
     */
    @GET("/calling/getCallingStatus")
    Observable<BaseDataResponse<CallingStatusEntity>> getCallingStatus(
            @Query("roomId") Integer roomId //房间号
    );

    /**
     * 获取房间状态，用于检查是否已解散
     *
     * @param roomId
     * @return
     */
    @GET("/calling/getRoomStatus")
    Observable<BaseDataResponse<CallingStatusEntity>> getRoomStatus(
            @Query("roomId") Integer roomId //房间号
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.IMTransUserEntity>>
     * @Desc TODO(IM用户Id转成数值id)
     * @author 彭石林
     * @parame [IMUserId]
     * @Date 2022/4/2
     */
    @FormUrlEncoded
    @POST("api/im/transUser")
    Observable<BaseDataResponse<IMTransUserEntity>> transUserIM(@Field("imId") String IMUserId);

    /**
     * @return io.reactivex.Observable<com.dl.play.chat.data.source.http.response.BaseDataResponse < com.dl.play.chat.entity.ChatDetailCoinEntity>>
     * @Desc TODO(拨打完成后调用查询总钻石 。 拨打发调用)
     * @author 彭石林
     * @parame [dismissRoom]
     * @Date 2022/3/21
     */
    @GET("calling/userAccount/getTotalCoins")
    Observable<BaseDataResponse<ChatDetailCoinEntity>> getTotalCoins(@Query("dismissRoom") Integer dismissRoom);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(游戏支付成功验签)
     * @author 彭石林
     * @parame [packageName, orderNumber, productId, token, type, event, serverId, roleId]
     * @Date 2022/2/10
     */
    @FormUrlEncoded
    @POST("api/order/googleNotify")
    Observable<BaseResponse> GamePaySuccessNotify(
            @Field("package_name") String packageName,
            @Field("order_number") String orderNumber,
            @Field("product_id[]") List<String> productId,
            @Field("token") String token,
            @Field("type") int type,
            @Field("event") Integer event,
            @Field("serverId") String serverId,
            @Field("roleId") String roleId
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.GamePhotoAlbumEntity>>
     * @Desc TODO()
     * @author 彭石林
     * @parame [serverId, roleId]
     * @Date 2022/1/21
     */
    @GET("/calling/albumImage/list")
    Observable<BaseDataResponse<GamePhotoAlbumEntity>> getGamePhotoAlbumList(@Query("serverId") String serverId, @Query("roleId") String roleId);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(游戏在线状态 1在线 - 1离线)
     * @author 彭石林
     * @parame [gameState]
     * @Date 2022/1/15
     */
    @FormUrlEncoded
    @POST("api/game/state")
    Observable<BaseResponse> setGameState(@Field("gameState") int gameState);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(serverId 是 String 区服ID ， 最多45个字符
     *roleId 是 String 角色ID ， 最多45个字符
     *roleName 是 String 角色名称 ， 最多100个字符
     *avatarUrl 是 String 角色头像URL地址 ， 最多1000个字符)
     * @author 彭石林
     * @parame []
     * @Date 2022/1/14
     */
    @Headers("Content-Type: application/json")
    @POST("/calling/gameRole/commitRoleInfo")
    Observable<BaseResponse> commitRoleInfo(@Body RequestBody requestBody);

    /**
     * 第三方登录
     *
     * @param id   唯一ID
     * @param type 登录类型 facebook/line
     * @return
     */
    @FormUrlEncoded
    @POST("api/auth/login")
    Observable<BaseDataResponse<UserDataEntity>> authLoginPost(
            @Field("id") String id,
            @Field("type") String type
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(修改用户性别)
     * @author 彭石林
     * @parame [sex]
     * @Date 2022/1/12
     */
    @FormUrlEncoded
    @POST("api/user/sex")
    Observable<BaseResponse> upUserSex(@Field("sex") Integer sex);

    /**
     * 聊天页面创建订单
     *
     * @param id      id 用户ID     当type为10时id传当前登陆用户id  当type为11时id传要解锁的用户ID
     * @param type    下单类型 1充值 2会员 3相册付费 4照片红包 5私聊 8发布动态 9发布节目 10一健打招呼钻石支付 11解锁社交账号
     * @param payType 1/余额支付 2/google支付 3/my_card支付 4/苹果支付
     * @return
     */
    @FormUrlEncoded
    @POST("api/order")
    Observable<BaseDataResponse<CreateOrderEntity>> createChatDetailOrder(
            @Field("id") Integer id,
            @Field("type") Integer type,
            @Field("pay_type") Integer payType,
            @Field("toUserId") Integer toUserId,
            @Field("channel") Integer channel
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.PriceConfigEntity.Current>>
     * @Desc TODO(男生获取未读消息 \ 清空)
     * @author 彭石林
     * @parame [toUserId, type]
     * @Date 2021/12/29
     */
    @GET("api/refundMsg")
    Observable<BaseDataResponse<PriceConfigEntity.Current>> getMaleRefundMsg(@Query("to_user_id") Integer toUserId, @Query("type") Integer type);


    /**
     * 余额不足提示
     *
     * @param toUserId
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST("api/tips")
    Observable<BaseDataResponse> getTips(@Field("to_user_id") Integer toUserId, @Field("type") Integer type, @Field("is_show") String isShow);

    /**
     * IM通话中追踪
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("api/collect")
    Observable<BaseResponse> addIMCollect(
            @Field("user_id") Integer userId,
            @Field("type") Integer type);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < java.util.Map < java.lang.String, java.lang.Integer>>>
     * @Desc TODO(真人 / 女神提醒)
     * @author 彭石林
     * @parame [toUserId]
     * @Date 2021/12/24
     */
    @GET("api/goddessTips")
    Observable<BaseDataResponse<Map<String, Integer>>> verifyGoddessTips(@Query("to_user_id") Integer toUserId);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse>
     * @Desc TODO(IM价格配置)
     * @author 彭石林
     * @parame [to_user_id]
     * @Date 2021/12/20
     */
    @GET("api/priceConfig")
    Observable<BaseDataResponse<PriceConfigEntity>> getPriceConfig(@Query("to_user_id") Integer to_user_id);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.CallingInfoEntity.SayHiList>>
     * @Desc TODO(破冰文案列表)
     * @author 彭石林
     * @parame [page, perPage]
     * @Date 2021/12/18
     */
    @GET("/calling/listSayHis")
    Observable<BaseDataResponse<CallingInfoEntity.SayHiList>> getSayHiList(@Query("page") Integer page, @Query("perPage") Integer perPage);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.CallingInfoEntity>>
     * @Desc TODO(通话中)
     * @author 彭石林
     * @parame [roomId, callingType, fromUserId, toUserId, currentUserId]
     * @Date 2021/12/13
     */
    @GET("/calling/getCallingInfo/v2")
    Observable<BaseDataResponse<CallingInfoEntity>> getCallingInfo(
            @Query("roomId") Integer roomId, //房间号
            @Query("callingType") Integer callingType, //通话类型：1=语音，2=视频
            @Query("inviterImId") String inviterImId, //拔打人用户ID
            @Query("receiverImId") String toUserId//接收人用户ID
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.entity.CallingInviteInfo>
     * @Desc TODO(IM聊天页面 拔打中 / 接收中)
     * @author 彭石林
     * @parame [appId, callingType, fromUserId, toUserId, currentUserId]
     * @Date 2021/12/13
     */
    @GET("/calling/getCallingInvitedInfo/v2")
    Observable<BaseDataResponse<CallingInviteInfo>> callingInviteInfo(
            @Query("callingType") Integer callingType,
            @Query("inviterImId") String inviterImId,
            @Query("receiverImId") String receiverImId
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(发送礼物)
     * @author 彭石林
     * @parame [gift_id, to_user_id, amount]
     * @Date 2021/12/9
     */
    @Headers("Content-Type: application/json")
    @POST("/calling/gift/sendGift")
    Observable<BaseResponse> sendUserGift(@Body RequestBody requestBody);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.GiftBagEntity>>
     * @Desc TODO(礼物背包接口)
     * @author 彭石林
     * @parame []
     * @Date 2021/12/7
     */
    @GET("api/gift")
    Observable<BaseDataResponse<GiftBagEntity>> getBagGiftInfo();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse>
     * @Desc TODO(用户收益页面)
     * @author 彭石林
     * @parame []
     * @Date 2021/12/6
     */
    @GET("calling/userProfit/getUserProfitPageInfo")
    Observable<BaseDataResponse<UserProfitPageEntity>> getUserProfitPageInfo(@Query("currentUserId") Long currentUserId, @Query("page") Integer page, @Query("perPage") Integer perPage);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.CoinWalletEntity>>
     * @Desc TODO(用户账户余额)
     * @author 彭石林
     * @parame []
     * @Date 2021/12/6
     */
    @GET("api/account")
    Observable<BaseDataResponse<CoinWalletEntity>> getUserAccount();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.GameCoinWalletEntity>>
     * @Desc TODO(用户账户余额)
     * @author KL
     * @parame []
     */
    @GET("calling/userAccount/getUserAccountPageInfo")
    Observable<BaseDataResponse<GameCoinWalletEntity>> getUserAccountPageInfo();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.BubbleEntity>>
     * @Desc TODO(回复收益气泡)
     * @author 彭石林
     * @parame []
     * @Date 2021/12/2
     */
    @GET("api/bubble")
    Observable<BaseDataResponse<BubbleEntity>> getBubbleEntity();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.AccostEntity>>
     * @Desc TODO(获取批量搭讪用户列表)
     * @author 彭石林
     * @parame []
     * @Date 2021/11/30
     */
    @GET("api/accost")
    Observable<BaseDataResponse<AccostEntity>> getAccostList(@Query("page") Integer page);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(批量搭讪)
     * @author 彭石林
     * @parame [userIds]
     * @Date 2021/11/30
     */
    @POST("api/accost")
    @FormUrlEncoded
    Observable<BaseResponse> putAccostList(@Field("user_ids[]") List<Integer> userIds);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(单次搭讪接口)
     * @author 彭石林
     * @parame [userId]
     * @Date 2021/11/30
     */
    @POST("api/accost/first")
    @FormUrlEncoded
    Observable<BaseResponse> putAccostFirst(@Field("user_id") Integer userId);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < com.dl.playfun.entity.ParkItemEntity>>
     * @Desc TODO(电台首页)
     * @author 彭石林
     * @parame [
     * sex, 性别 1男 0女
     * city_id, 城市ID
     * 游戏渠道 1喵遊 2杜拉克
     * is_online, 在线状态 0/1
     * is_collect, 追蹤的人 0不看 1看
     * type 类别 1按发布时间 2按活动时间]
     * @Date 2021/10/26
     */
    @GET("api/v2/broadcast/home")
    Observable<BaseDataResponse<BroadcastListEntity>> getBroadcastHome(
            @Query("sex") Integer sex,
            @Query("city_id") Integer city_id,
            @Query("game_channel") Integer game_id,
            @Query("is_online") Integer is_online,
            @Query("is_collect") Integer is_collect,
            @Query("type") Integer type,
            @Query("page") Integer page
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < com.dl.playfun.entity.MessageRuleEntity>>
     * @Desc TODO(获取聊天 （ 相册 、 评价发送规则 ）)
     * @author 彭石林
     * @parame []
     * @Date 2021/10/22
     */
    @GET("api/v2/user/rule")
    Observable<BaseDataResponse<List<MessageRuleEntity>>> getMessageRule();

    /**
     * 屏蔽關鍵字
     *
     * @return
     */
    @GET("/calling/config/getSensitiveWords")
    Observable<BaseDataResponse> getSensitiveWords();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse>
     * @Desc TODO(IM聊天相册)
     * @author 彭石林
     * @parame [user_id]
     * @Date 2021/10/22
     */
    @GET("api/user/im/image")
    Observable<BaseDataResponse<PhotoAlbumEntity>> getPhotoAlbum(@Query("user_id") Integer user_id);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Desc TODO(删除用户录音)
     * @author 彭石林
     * @parame []
     * @Date 2021/10/25
     */
    @GET("api/v2/userSound/del")
    Observable<BaseResponse> removeUserSound();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse>
     * @Desc TODO(上传录音文案)
     * @author 彭石林
     * @parame [paht]
     * @Date 2021/10/21
     */
    @FormUrlEncoded
    @POST("api/v2/userSound")
    Observable<BaseDataResponse> putUserSound(@Field("sound") String paht, @Field("sound_time") Integer sound_time);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse>
     * @Desc TODO(查询录音文案)
     * @author 彭石林
     * @parame []
     * @Date 2021/10/21
     */
    @GET("api/v2/userSound")
    Observable<BaseListDataResponse<SoundEntity>> getUserSound(@Query("page") Integer page);

    /**
     * 发布节目
     *
     * @param theme_id     节目主题ID
     * @param describe     约会内容
     * @param address      地址
     * @param hope_object  期望对象 例：1,2,3,4
     * @param start_date   开始日期
     * @param end_time     结束时间
     * @param images       图片
     * @param is_comment   禁止评论(0/1)
     * @param is_hide      对同性隐藏(0/1)
     * @param address_name 地址名称
     * @param city_id      城市id
     * @param longitude    经度
     * @param latitude     纬度
     * @return
     */
    @FormUrlEncoded
    @POST("api/topical/create")
    Observable<BaseResponse> topicalCreate(
            @Field("theme_id") Integer theme_id,
            @Field("content") String describe,
            @Field("address") String address,
            @Field("hope_object[]") List<Integer> hope_object,
            @Field("start_date") String start_date,
            @Field("end_time") Integer end_time,
            @Field("images[]") List<String> images,
            @Field("is_comment") Integer is_comment,
            @Field("is_hide") Integer is_hide,
            @Field("address_name") String address_name,
            @Field("city_id") Integer city_id,
            @Field("longitude") Double longitude,
            @Field("latitude") Double latitude,
            @Field("video") String video

    );

    /**
     * 发布心情
     *
     * @param describe   约会内容
     * @param start_date 开始日期
     * @param images     图片
     * @param is_comment 禁止评论(0/1)
     * @param is_hide    对同性隐藏(0/1)
     * @param longitude  经度
     * @param latitude   纬度
     * @param video      视频
     * @return
     */
    @FormUrlEncoded
    @POST("api/news/create")
    Observable<BaseResponse> topicalCreateMood(
            @Field("content") String describe,
            @Field("start_date") String start_date,
            @Field("images[]") List<String> images,
            @Field("is_comment") Integer is_comment,
            @Field("is_hide") Integer is_hide,
            @Field("longitude") Double longitude,
            @Field("latitude") Double latitude,
            @Field("video") String video,
            @Field("news_type") Integer news_type
    );

    /**
     * 我的动态-全部
     *
     * @param page 页码
     * @return
     */
    @GET("api/v2/broadcast/news")
    Observable<BaseListDataResponse<BroadcastEntity>> broadcastAll(@Query("page") Integer page);

    /**
     * 商品列表
     *
     * @param type 类型 vip:升级会员 recharge:充值 points积分商品
     * @return
     */
    @GET("api/goods")
    Observable<BaseDataResponse<List<GoodsEntity>>> pointsGoodList(@Query("type") String type);

    /**
     * 推送状态提交 type  1今日 2钻石 3VIP
     *
     * @return
     */
    @POST("api/pushGreet")
    @FormUrlEncoded
    Observable<BaseResponse> pushGreet(@Field("type") Integer type);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < java.util.Map < java.lang.String, java.lang.String>>>
     * @Desc TODO(查询用户是否在黑名单里面)
     * @author 彭石林
     * @parame [userId]
     * @Date 2021/9/17
     */
    @GET("api/blacklist/isBlacklist")
    Observable<BaseDataResponse<Map<String, String>>> isBlacklist(@Query("to_user_id") String userId);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < com.dl.playfun.entity.TaskAdEntity>>
     * @Desc TODO(查询会员中心配置)
     * @author 彭石林
     * @parame []
     * @Date 2021/9/14
     */
    @GET("api/v2/user/recharge")
    Observable<BaseListDataResponse<TaskAdEntity>> rechargeVipList();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < java.util.Map < java.lang.String, java.lang.String>>>
     * @Desc TODO(查询用户是否离线)
     * @author 彭石林
     * @parame []
     * @Date 2021/9/9
     */
    @GET("api/v2/user/isOnline")
    Observable<BaseDataResponse<Map<String, String>>> isOnlineUser(@Query("user_id") String userId);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < BrowseNumberEntity>>
     * @Desc TODO(新增谁看我及粉丝数)
     * @author 彭石林
     * @parame []
     * @Date 2021/8/4
     */
    @GET("api/v2/user/newsBrowseNumber")
    Observable<BaseDataResponse<BrowseNumberEntity>> newsBrowseNumber();

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < com.dl.playfun.entity.TraceEntity>>
     * @Desc TODO(谁看过我列表)
     * @author 彭石林
     * @parame [page]
     * @Date 2021/8/4
     */
    @GET("api/collect/toBrowse")
    Observable<BaseListDataResponse<TraceEntity>> toBrowse(@Query("page") Integer page);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < com.dl.playfun.entity.TraceEntity>>
     * @Desc TODO(查询粉丝列表)
     * @author 彭石林
     * @parame [page]
     * @Date 2021/8/3
     */
    @GET("api/collect/fans")
    Observable<BaseListDataResponse<TraceEntity>> collectFans(@Query("page") Integer page);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseListDataResponse < TraceEntity>>
     * @Desc TODO(查询追踪列表)
     * @author 彭石林
     * @parame [page]
     * @Date 2021/8/3
     */
    @GET("api/collect")
    Observable<BaseListDataResponse<TraceEntity>> collect(@Query("page") Integer page);

    /**
     * 主动上报用户当前定位信息。用于完善用户资料
     *
     * @param latitude
     * @param longitude
     * @return
     */
    @POST("api/user/coordinate")
    Observable<BaseResponse> reportUserLocation(@Query("latitude") String latitude, @Query("longitude") String longitude);

    /**
     * 查询本地历史谷歌支付订单。提交给后台
     *
     * @param map
     * @return
     */
    @POST("api/v2/order/diff")
    Observable<BaseResponse> repoetLocalGoogleOrder(@Body Map<String, Object> map);


    /**
     * 创建订单
     *
     * @param id      id 用户ID     当type为10时id传当前登陆用户id  当type为11时id传要解锁的用户ID
     * @param type    下单类型 1充值 2会员 3相册付费 4照片红包 5私聊 8发布动态 9发布节目 10一健打招呼钻石支付 11解锁社交账号
     * @param payType 1/余额支付 2/google支付 3/my_card支付 4/苹果支付
     * @return
     */
    @FormUrlEncoded
    @POST("api/order")
    Observable<BaseDataResponse<CreateOrderEntity>> createOrderUserDetail(
            @Field("id") Integer id,
            @Field("type") Integer type,
            @Field("pay_type") Integer payType,
            @Field("number") Integer number
    );

    /**
     * 免费订阅7天会员
     *
     * @param pay_type
     * @return
     */
    @POST("api/v2/order")
    Observable<BaseDataResponse<Map<String, String>>> freeSevenDay(@Query("pay_type") Integer pay_type, @Query("goods_type") Integer goods_type);

    /**
     * 用户标签
     *
     * @param to_user_id
     * @return
     */
    @GET("api/v2/user/tag")
    Observable<BaseDataResponse<TagEntity>> tag(@Query("to_user_id") String to_user_id);

    /**
     * 绑定用户关系
     *
     * @param code
     * @param type 类型 1.appsflyer 2.用户注册填写 3用户注册后填写
     * @return
     */
    @POST("api/inviteCode")
    Observable<BaseResponse> userInvite(@Query("code") String code, @Query("type") Integer type, @Query("channel") String channel);

    /**
     * 绑定用户城市
     *
     * @param city_id
     * @return
     */
    @POST("api/v2/city")
    Observable<BaseResponse> isBindCity(@Query("city_id") Integer city_id);

    /**
     * 注册用户（简化流程）
     *
     * @param nickname
     * @param avatar
     * @param birthday
     * @param sex
     * @return
     */
    @POST("api/v2/user")
    Observable<BaseDataResponse<UserDataEntity>> regUser(@Query("nickname") String nickname, @Query("avatar") String avatar, @Query("birthday") String birthday, @Query("sex") Integer sex);

    /**
     * 上报用户当前坐标
     *
     * @param latitude
     * @return
     */
    @POST("api/user/coordinate")
    Observable<BaseResponse> coordinate(@Query("latitude") Double latitude, @Query("longitude") Double longitud, @Query("county_name") String county_name, @Query("province_name") String province_name);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.SwiftMessageEntity>>
     * @Author 彭石林
     * @Description 获取聊天信息快捷语
     * @Date 2021/4/30 16:07
     * @Phone 16620350375
     * @email 15616314565@163.com
     * Param []
     **/
    @GET("api/chat/text")
    Observable<BaseDataResponse<SwiftMessageEntity>> getSwiftMessage(@Query("page") Integer page);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseResponse>
     * @Author 彭石林
     * @Description 绑定第三方账号
     * @Date 2021/4/29 15:33
     * @Phone 16620350375
     * @email 15616314565@163.com
     * Param [id, type]
     **/
    @POST("/api/auth/bindAccount")
    Observable<BaseResponse> bindAccount(@Query("id") String id, @Query("type") String type);

    /**
     * 真人人脸图片
     *
     * @param imgUrl 阿里云图片Url
     * @return
     */
    @POST("/api/aliyun/compareFaces")
    Observable<BaseDataResponse<Map<String, String>>> imagFaceUpload(
            @Query("img") String imgUrl
    );

    /**
     * @return io.reactivex.Observable<com.dl.playfun.data.source.http.response.BaseDataResponse < com.dl.playfun.entity.UserDataEntity>>
     * @Author 彭石林
     * @Description 第三方登录
     * @Date 2021/4/27 17:30
     * @Phone 16620350375
     * @email 15616314565@163.com
     * Param [phone, code]
     **/
    @POST("api/v2/login")
    Observable<BaseDataResponse<UserDataEntity>> v2Login(@Query("phone") String phone, @Query("code") String code, @Query("device_code") String device_code);

    /**
     * @return io.reactivex.Observable<com.dl.playfun.entity.VersionEntity>
     * @Author 彭石林
     * @Description 后台效验检测更新
     * @Date 2021/3/31 19:32
     * @Phone 16620350375
     * @email 15616314565@163.com
     * Param [client]
     **/
    @GET("/api/version")
    Observable<BaseDataResponse<VersionEntity>> detectionVersion(@Query("client") String client);

    /**
     * GoogleMaps附近搜索
     *
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("/calling/googleMapApi/nearSearchPlace")
    Observable<BaseDataResponse<GoogleNearPoiBean>> nearSearchPlace(@Body RequestBody requestBody);

    /**
     * GoogleMaps文本搜索
     *
     * @return
     */
    @POST("/calling/googleMapApi/textSearchPlace")
    Observable<BaseDataResponse<GooglePoiBean>> textSearchPlace(@Body RequestBody requestBody);

    /**
     * 短信验证码接口
     *
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("api/verify-code")
    Observable<BaseResponse> verifyCodePost(
            @Field("phone") String phone
    );

    /**
     * 注册
     *
     * @param phone    电话号码
     * @param password 密码
     * @param code     验证码
     * @return
     */
    @FormUrlEncoded
    @POST("api/register")
    Observable<BaseDataResponse<TokenEntity>> registerPost(
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("code") String code
    );

    /**
     * 是否同意用户协议
     *
     * @param accept
     * @return
     */
    @FormUrlEncoded
    @POST("api/contract")
    Observable<BaseResponse> acceptUseAgreement(
            @Field("is_contract") Integer accept
    );

    /**
     * 登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("api/login")
    Observable<BaseDataResponse<TokenEntity>> loginPost(
            @Field("phone") String phone,
            @Field("password") String password
    );

    /**
     * 首页列表
     *
     * @param cityId     城市ID
     * @param type       1附近 2最新注册 3女神
     * @param isOnline   在线优先
     * @param sex        性别 1男 0女
     * @param searchName 昵称/职业来搜索
     * @param longitude  经度
     * @param latitude   纬度
     * @return
     */
    @GET("api/home")
    Observable<BaseListDataResponse<ParkItemEntity>> homeListGet(
            @Query("city_id") Integer cityId,
            @Query("type") Integer type,
            @Query("is_online") Integer isOnline,
            @Query("sex") Integer sex,
            @Query("search_name") String searchName,
            @Query("longitude") Double longitude,
            @Query("latitude") Double latitude,
            @Query("page") Integer page
    );

    /**
     * 用户中心
     *
     * @return 邀请码版本 0原有版本 1新版本
     */
    @GET("api/user/info")
    Observable<BaseDataResponse<UserInfoEntity>> getUserInfo(@Query("invite_version") Integer invite_version);

    /**
     * 个人资料
     *
     * @return
     */
    @GET("api/user/data")
    Observable<BaseDataResponse<UserDataEntity>> getUserData(@Query("invite_version") Integer invite_version);

    /**
     * 给用户加备注
     *
     * @param userId   要加备注的用户ID
     * @param nickname 名称
     * @param remark   备注说明
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/remark")
    Observable<BaseResponse> userRemark(
            @Field("user_id") Integer userId,
            @Field("nickname") String nickname,
            @Field("remark") String remark
    );

    /**
     * 获取用户备注
     *
     * @param userId
     * @return
     */
    @GET("api/user/remark")
    Observable<BaseDataResponse<UserRemarkEntity>> getUserRemark(
            @Query("user_id") Integer userId
    );

    /**
     * 修改头像
     *
     * @param avatar
     * @return
     */
    @FormUrlEncoded
    @POST("api/user")
    Observable<BaseResponse> updateAvatar(
            @Field("avatar") String avatar
    );

    /**
     * 修改个人资料
     *
     * @param nickname           昵称
     * @param permanent_city_ids 常驻城市 格式如:6,7,8
     * @param birthday           生日
     * @param occupation_id      职业
     * @param program_ids        交友节目 格式如:1,2,3
     * @param hope_object_ids    期望对象 格式如3,4,5
     * @param is_weixin_show     是否显示微信
     * @param height             身高cm
     * @param weight             体重kg
     * @param desc               个人介绍
     * @return
     */
    @FormUrlEncoded
    @POST("api/user")
    Observable<BaseResponse> updateUserData(
            @Field("nickname") String nickname,
            @Field("permanent_city_ids[]") List<Integer> permanent_city_ids,
            @Field("birthday") String birthday,
            @Field("occupation_id") String occupation_id,
            @Field("program_ids[]") List<Integer> program_ids,
            @Field("hope_object_ids[]") List<Integer> hope_object_ids,
            @Field("facebook") String facebook,
            @Field("insgram") String insgram,
            @Field("account_type") Integer accountType,
            @Field("is_weixin_show") Integer is_weixin_show,
            @Field("height") Integer height,
            @Field("weight") Integer weight,
            @Field("desc") String desc
    );

    /**
     * 个人主页
     *
     * @param id 用户id
     * @return
     */
    @GET("api/user/{id}")
    Observable<BaseDataResponse<UserDetailEntity>> userMain(
            @Path("id") int id,
            @Query("longitude") Double longitude,
            @Query("latitude") Double latitude
    );


    /**
     * 节目详情
     *
     * @param id 节目ID
     * @return
     */
    @GET("api/topical/{id}")
    Observable<BaseDataResponse<TopicalListEntity>> topicalDetail(@Path("id") Integer id);

    /**
     * 报名
     *
     * @param id  节目ID
     * @param img 照片
     * @return
     */
    @FormUrlEncoded
    @POST("api/topical/signUp")
    Observable<BaseResponse> singUp(
            @Field("id") Integer id,
            @Field("img") String img
    );

    /**
     * 黑名单列表
     *
     * @return
     */
    @GET("api/blacklist")
    Observable<BaseListDataResponse<BlackEntity>> getBlackList(@Query("page") Integer page);

    /**
     * 加入黑名单
     *
     * @param user_id
     * @return
     */
    @FormUrlEncoded
    @POST("api/blacklist")
    Observable<BaseResponse> addBlack(
            @Field("user_id") Integer user_id);

    /**
     * 删除黑名单
     *
     * @param id
     * @return
     */
    @DELETE("api/blacklist/{id}")
    Observable<BaseResponse> deleteBlack(
            @Path("id") Integer id
    );

    /**
     * 删除节目
     *
     * @param id
     * @return
     */
    @DELETE("api/topical/{id}")
    Observable<BaseResponse> deleteTopical(
            @Path("id") Integer id
    );

    /**
     * 喜欢列表
     *
     * @return
     */
    @GET("api/collect")
    Observable<BaseListDataResponse<ParkItemEntity>> getCollectList(
            @Query("page") Integer page,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

    /**
     * 加入喜欢
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("api/collect")
    Observable<BaseResponse> addCollect(
            @Field("user_id") Integer userId);

    /**
     * 删除喜欢
     *
     * @param userId
     * @return
     */
    @DELETE("api/collect/{user_id}")
    Observable<BaseResponse> deleteCollect(
            @Path("user_id") Integer userId
    );

    /**
     * 发布动态
     *
     * @param content    内容
     * @param images     图片
     * @param is_comment 禁止评论（0/1）
     * @param is_hide    对同性隐藏（0/1
     * @return
     */
    @FormUrlEncoded
    @POST("api/news/create")
    Observable<BaseResponse> newsCreate(
            @Field("content") String content,
            @Field("images[]") List<String> images,
            @Field("is_comment") Integer is_comment,
            @Field("is_hide") Integer is_hide
    );

    /**
     * 动态详情
     *
     * @param id
     * @return
     */
    @GET("api/news/{id}")
    Observable<BaseDataResponse<NewsEntity>> newsDetail(@Path("id") Integer id);

    /**
     * 删除动态
     *
     * @param id
     * @return
     */
    @DELETE("api/news/{id}")
    Observable<BaseResponse> deleteNews(
            @Path("id") Integer id
    );


    /**
     * 电台首页
     *
     * @param type      1按发布时间 2按活动时间
     * @param theme_id  主题ID
     * @param is_online 按在线状态 排序
     * @param city_id   城市 ID
     * @param sex       性别 1男 0女
     * @return
     */
    @GET("api/v2/broadcast")
    Observable<BaseListDataResponse<BroadcastEntity>> broadcast(
            @Query("type") Integer type,
            @Query("theme_id") Integer theme_id,
            @Query("is_online") Integer is_online,
            @Query("city_id") Integer city_id,
            @Query("sex") Integer sex,
            @Query("page") Integer page);

    /**
     * 动态列表
     *
     * @param user_id 不传为当前登陆用户信息
     * @return
     */
    @GET("api/news")
    Observable<BaseListDataResponse<NewsEntity>> getNewsList(
            @Query("user_id") Integer user_id,
            @Query("page") Integer page);


    /**
     * 节目列表
     *
     * @param user_id 不传为当前登陆用户信息
     * @return
     */
    @GET("/api/topical")
    Observable<BaseListDataResponse<TopicalListEntity>> getTopicalList(
            @Query("user_id") Integer user_id,
            @Query("page") Integer page);


    /**
     * 举报
     *
     * @param id        数据ID
     * @param type      类型 home 个人主页 broadcast电台
     * @param reason_id 原因ID
     * @param images    图片
     * @param desc      描述
     * @return
     */
    @FormUrlEncoded
    @POST("api/report/create")
    Observable<BaseResponse> report(
            @Field("id") Integer id,
            @Field("type") String type,
            @Field("reason_id") String reason_id,
            @Field("images[]") List<String> images,
            @Field("desc") String desc
    );

    /**
     * 节目评论
     *
     * @param id         节目 ID
     * @param content    评论内容
     * @param to_user_id 目标用户ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/topical/comment")
    Observable<BaseResponse> topicalComment(
            @Field("id") Integer id,
            @Field("content") String content,
            @Field("to_user_id") Integer to_user_id
    );

    /**
     * 动态评论
     *
     * @param id         评论 ID
     * @param content    评论内容
     * @param to_user_id 目标用户ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/news/comment")
    Observable<BaseResponse> newsComment(
            @Field("id") Integer id,
            @Field("content") String content,
            @Field("to_user_id") Integer to_user_id
    );


    /**
     * 是否可以评价
     *
     * @param userId
     * @return
     */
    @GET("api/evaluate/{id}")
    Observable<BaseDataResponse<StatusEntity>> evaluateStatus(
            @Path("id") Integer userId
    );

    /**
     * 写评价
     *
     * @param user_id 被评价人用户id
     * @param tag_id  评价标签ID
     * @param img     当tag_id值为 5/6时 为必填
     * @return
     */
    @FormUrlEncoded
    @POST("api/evaluate/create")
    Observable<BaseResponse> evaluateCreate(
            @Field("user_id") Integer user_id,
            @Field("tag_id") Integer tag_id,
            @Field("img") String img
    );

    /**
     * 我的真实评价
     *
     * @return
     */
    @GET("api/evaluate")
    Observable<BaseDataResponse<List<EvaluateEntity>>> evaluate(
            @Query("user_id") Integer userId
    );

    /**
     * 动态点赞
     *
     * @param id 动态ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/news/give")
    Observable<BaseResponse> newsGive(
            @Field("id") Integer id
    );

    /**
     * 是否可以私聊
     *
     * @param userId 跟谁私聊的用户ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/isChat")
    Observable<BaseDataResponse<IsChatEntity>> isChat(
            @Field("user_id") Integer userId
    );

    /**
     * @param userId 跟谁私聊的用户ID
     * @param type   类型 1私聊 2相册
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/useVip")
    Observable<BaseResponse> useVipChat(
            @Field("user_id") Integer userId,
            @Field("type") Integer type
    );

    /**
     * 阅后即焚接口
     *
     * @param imageId 图片ID
     * @return
     */
    @GET("api/image-read-log")
    Observable<BaseResponse> imgeReadLog(
            @Query("image_id") Integer imageId);

    /**
     * 修改密码
     *
     * @param original_password 原密码
     * @param new_password      新密码
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/password")
    Observable<BaseResponse> password(
            @Field("original_password") String original_password,
            @Field("new_password") String new_password
    );

    /**
     * 获取所有配置参数
     *
     * @return
     */
    @GET("api/config")
    Observable<BaseDataResponse<AllConfigEntity>> getAllConfig();

    /**
     * 获取系统配置
     *
     * @param type 类型 program_time：节目时间 height:身高 weight:体重 report_reason：举报原因 evaluate:评价标签 hope_object:期望对象 city:城市
     * @return
     */
    @GET("api/config")
    Observable<BaseDataResponse<List<ConfigItemEntity>>> getSystemConfig(
            @Query("type") String type
    );

    /**
     * 获取职业配置
     *
     * @param type
     * @return
     */
    @GET("api/config")
//@GET("api/config")
    Observable<BaseDataResponse<List<OccupationConfigItemEntity>>> getOccupationConfig(
            @Query("type") String type
    );

    /**
     * 获取主题配置
     *
     * @param type
     * @return
     */
    @GET("api/config")
    Observable<BaseDataResponse<List<ConfigItemEntity>>> getThemeConfig(
            @Query("type") String type
    );

    /**
     * 获取游戏名称和地区
     *
     * @param type
     * @return
     */
    @GET("api/config")
    Observable<BaseDataResponse<List<RadioTwoFilterItemEntity>>> getGameCity(
            @Query("type") String type
    );


    /**
     * 申请浏览相册
     *
     * @param user_id 用户ID
     * @param img     图片ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/verify")
    Observable<BaseResponse> userVerify(
            @Field("user_id") Integer user_id,
            @Field("img") String img
    );

    /**
     * 商品列表
     *
     * @param type 类型 vip:升级会员 recharge:充值
     * @return
     */
    @GET("api/goods")
    Observable<BaseDataResponse<List<GoodsEntity>>> goods(
            @Query("type") String type);


    /**
     * 商品列表
     *
     * @param type 类型 vip:升级会员 recharge:充值
     * @return
     */
    @GET("api/goods")
    Observable<BaseDataResponse<List<VipPackageItemEntity>>> vipPackages(
            @Query("type") String type);


    /**
     * 文件图片上传
     *
     * @param file 用户ID
     * @return
     */
    @Multipart
    @POST("api/upload")
    Observable<BaseDataResponse<String>> imagUpload(
            @Part MultipartBody.Part file
    );

    /**
     * 保存验证图上
     *
     * @param imgPath
     * @return
     */
    @FormUrlEncoded
    @POST("api/aliyun/upload")
    Observable<BaseDataResponse> saveVerifyFaceImage(
            @Field("img") String imgPath
    );

    /**
     * 提现
     *
     * @param money 提现金额
     * @return
     */
    @FormUrlEncoded
    @POST("api/cashOut")
    Observable<BaseResponse> cashOut(
            @Field("money") float money);

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @return
     */
    @FormUrlEncoded
    @POST("api/verify-code")
    Observable<BaseResponse> sendCcode(
            @Field("phone") String phone
    );


    /**
     * 相册详情
     *
     * @param userId
     * @param type   1圖片 2視頻
     * @return
     */
    @GET("api/user/AlbumImage")
    Observable<BaseListDataResponse<AlbumPhotoEntity>> albumImage(
            @Query("user_id") Integer userId,
            @Query("type") Integer type
    );

    /**
     * 修改相片属性
     *
     * @param imgId 相片ID
     * @param type  1开启阅后即焚 2红包图片 3紅包視頻
     * @param state 状态 0否 1是
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/AlbumImage")
    Observable<BaseResponse> setAlbumImage(
            @Field("id") Integer imgId,
            @Field("type") Integer type,
            @Field("boolean") Integer state
    );

    /**
     * 上传到相册
     *
     * @param fileType   1图片 2视频
     * @param src        图片地址
     * @param isBurn     是否閱後即焚
     * @param videoImage 視頻首
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/AlbumImage/insert")
    Observable<BaseResponse> albumInsert(
            @Field("file_type") Integer fileType,
            @Field("src") String src,
            @Field("is_burn") Integer isBurn,
            @Field("video_img") String videoImage

    );

    /**
     * 删除相册图片
     *
     * @param id
     * @return
     */
    @DELETE("api/user/AlbumImage/{id}")
    Observable<BaseDataResponse<List<AlbumPhotoEntity>>> delAlbumImage(
            @Path("id") Integer id
    );


    /**
     * 查看真人认证结果
     *
     * @param bizId
     * @return
     */
    @GET("api/aliyun/verifyResult/{BizId}")
    Observable<BaseDataResponse<FaceVerifyResultEntity>> faceVerifyResult(
            @Path("BizId") String bizId
    );

    /**
     * 获取用户认证状态
     *
     * @return
     */
    @GET("api/user/isCertification")
    Observable<BaseDataResponse<StatusEntity>> faceIsCertification();

    /**
     * 获取用户隐私设置
     *
     * @return
     */
    @GET("api/user/privacy")
    Observable<BaseDataResponse<PrivacyEntity>> getPrivacy();

    /**
     * 保存用户隐私设置
     *
     * @param isHome       在公园列表隐藏我(0/1)
     * @param isDistance   对他人隐藏我的距离(0/1)
     * @param isOnlineTIme 对他们隐藏我的在线时间(0/1)
     * @param isNearby     3KM內隱藏我(0/1)
     * @param isConnection 允许有私聊权限的人对我发起连麦(0/1)
     * @return
     */

    @FormUrlEncoded
    @POST("api/user/privacy")
    Observable<BaseResponse> setPrivacy(
            @Field("is_home") Integer isHome,
            @Field("is_distance") Integer isDistance,
            @Field("is_online_time") Integer isOnlineTIme,
            @Field("is_connection") Integer isConnection,
            @Field("is_nearby") Integer isNearby
    );

    /**
     * 修改手机号
     *
     * @param phone    手机号
     * @param code     验证码
     * @param password 密码
     * @return
     */

    @FormUrlEncoded
    @POST("api/user/phone")
    Observable<BaseResponse> updatePhone(
            @Field("phone") String phone,
            @Field("code") int code,
            @Field("password") String password
    );

    /**
     * 申请女神
     *
     * @param images
     * @return
     */
    @FormUrlEncoded
    @POST("api/apply_goddess")
    Observable<BaseResponse> applyGoddess(
            @Field("images[]") List<String> images
    );

    /**
     * 查看申请女神状态
     * status 状态 -1未申请 0等待审核 1审核通过 2未通过
     *
     * @return
     */
    @GET("api/apply_goddess/show")
    Observable<BaseDataResponse<StatusEntity>> applyGoddessResult();

    /**
     * 重置密码
     *
     * @param phone    手机号
     * @param code     验证码
     * @param password 密码
     * @return
     */

    @FormUrlEncoded
    @POST("api/resetPassword")
    Observable<BaseResponse> resetPassword(
            @Field("phone") String phone,
            @Field("code") int code,
            @Field("password") String password
    );

    /**
     * 设置性别
     *
     * @param sex 0女 1男
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/sex")
    Observable<BaseResponse> setSex(
            @Field("sex") int sex
    );

    /**
     * 现金账户
     *
     * @return
     */
    @GET("api/userAccount/account")
    Observable<BaseDataResponse<CashWalletEntity>> cashWallet();

    /**
     * 币账户
     *
     * @return
     */
    @GET("api/userCoin/account")
    Observable<BaseDataResponse<CoinWalletEntity>> coinWallet();

    /**
     * 设置提现账号
     *
     * @param realName 收款人真实姓名
     * @param account  收款账户
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/account")
    Observable<BaseResponse> setWithdrawAccount(
            @Field("realname") String realName,
            @Field("account_number") String account
    );

    /**
     * 设置我的相册权限
     *
     * @param type  1公开 2付费解锁 3查看需要我验证
     * @param money 相册金额 当type为2才传入
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/album")
    Observable<BaseResponse> setAlbumPrivacy(
            @Field("type") Integer type,
            @Field("money") Integer money
    );

    /**
     * 开启/关闭评论
     *
     * @param id        电台ID
     * @param isComment 1关闭 0开启
     * @return
     */
    @FormUrlEncoded
    @POST("api/broadcast/isComment")
    Observable<BaseResponse> setComment(
            @Field("id") Integer id,
            @Field("isComment") Integer isComment
    );


    /**
     * 查看申请消息列表
     *
     * @return
     */
    @GET("api/message/apply")
    Observable<BaseListDataResponse<ApplyMessageEntity>> getMessageApply(
            @Query("page") Integer page
    );

    /**
     * 电台消息列表
     *
     * @return
     */
    @GET("api/message/broadcast")
    Observable<BaseListDataResponse<BoradCastMessageEntity>> getMessageBoradcast(
            @Query("page") Integer page
    );

    /**
     * 评论消息列表
     *
     * @return
     */
    @GET("api/message/comment")
    Observable<BaseListDataResponse<CommentMessageEntity>> getMessageComment(
            @Query("page") Integer page
    );

    /**
     * 评价消息列表
     *
     * @return
     */
    @GET("api/message/evaluate")
    Observable<BaseListDataResponse<EvaluateMessageEntity>> getMessageEvaluate(
            @Query("page") Integer page
    );

    /**
     * 点赞消息列表
     *
     * @return
     */
    @GET("api/message/give")
    Observable<BaseListDataResponse<GiveMessageEntity>> getMessageGive(
            @Query("page") Integer page
    );

    /**
     * 报名消息列表
     *
     * @return
     */
    @GET("api/message/sign")
    Observable<BaseListDataResponse<SignMessageEntity>> getMessageSign(
            @Query("page") Integer page
    );

    /**
     * 系统消息列表
     *
     * @return
     */
    @GET("api/message/system")
    Observable<BaseListDataResponse<SystemMessageEntity>> getMessageSystem(
            @Query("page") Integer page
    );

    /**
     * 收益消息列表
     *
     * @return
     */
    @GET("api/message/profit")
    Observable<BaseListDataResponse<ProfitMessageEntity>> getMessageProfit(
            @Query("page") Integer page
    );

    /**
     * 消息汇总列表
     *
     * @return
     */
    @GET("api/message")
    Observable<BaseDataResponse<List<MessageGroupEntity>>> getMessageList();


    /**
     * 评价上诉
     *
     * @param messageId
     * @param tagId
     * @return
     */
    @FormUrlEncoded
    @POST("api/message/evaluate/appeal")
    Observable<BaseResponse> evaluateAppeal(
            @Field("id") Integer messageId,
            @Field("tag_id") Integer tagId
    );

    /**
     * 删除申请查看消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/apply/{id}")
    Observable<BaseResponse> deleteMessageApply(
            @Path("id") Integer id
    );

    /**
     * 删除广播消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/broadcast/{id}")
    Observable<BaseResponse> deleteMessageBroadcast(
            @Path("id") Integer id
    );

    /**
     * 删除评论消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/comment/{id}")
    Observable<BaseResponse> deleteMessageComment(
            @Path("id") Integer id
    );

    /**
     * 删除评价消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/evaluate/{id}")
    Observable<BaseResponse> deleteMessageEvaluate(
            @Path("id") Integer id
    );

    /**
     * 删除点赞消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/give/{id}")
    Observable<BaseResponse> deleteMessageGive(
            @Path("id") Integer id
    );

    /**
     * 删除报名消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/sign/{id}")
    Observable<BaseResponse> deleteMessageSign(
            @Path("id") Integer id
    );

    /**
     * 删除系统消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/system/{id}")
    Observable<BaseResponse> deleteMessageSystem(
            @Path("id") Integer id
    );

    /**
     * 删除收益消息
     *
     * @param id 消息ID
     * @return
     */
    @DELETE("api/message/profit/{id}")
    Observable<BaseResponse> deleteMessageProfit(
            @Path("id") Integer id
    );


    /**
     * 获取推送设置
     *
     * @return
     */
    @GET("api/user/push")
    Observable<BaseDataResponse<PushSettingEntity>> getPushSetting();

    /**
     * 保存推送设置
     *
     * @param isChat       私聊消息通知(0/1)
     * @param isSign       广播报名通知(0/1)
     * @param isGive       新点赞提醒(0/1)
     * @param isComment    新评论提醒(0/1)
     * @param isBroadcast  新广播提醒(0/1)
     * @param isApply      用户通过了我的查看请求(0/1)
     * @param isInvitation 邀请码申请成功(0/1)
     * @param isSound      声音(0/1)
     * @param isShake      震动(0/1)
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/push")
    Observable<BaseResponse> savePushSetting(
            @Field("is_chat") Integer isChat,
            @Field("is_sign") Integer isSign,
            @Field("is_give") Integer isGive,
            @Field("is_comment") Integer isComment,
            @Field("is_broadcast") Integer isBroadcast,
            @Field("is_apply") Integer isApply,
            @Field("is_invitation") Integer isInvitation,
            @Field("is_sound") Integer isSound,
            @Field("is_shake") Integer isShake
    );

    /**
     * 节目点赞
     *
     * @param id 节目ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/topical/give")
    Observable<BaseResponse> TopicalGive(
            @Field("id") Integer id
    );

    /**
     * 结束报名
     *
     * @param id 节目ID
     * @return
     */
    @GET("api/topical/finish/{id}")
    Observable<BaseResponse> TopicalFinish(
            @Path("id") Integer id
    );

    /**
     * 阅后即焚恢复
     *
     * @return
     */
    @GET("api/user/burnReset")
    Observable<BaseResponse> burnReset();

    /**
     * 创建订单
     *
     * @param id      id 用户ID     当type为10时id传当前登陆用户id  当type为11时id传要解锁的用户ID
     * @param type    下单类型 1充值 2会员 3相册付费 4照片红包 5私聊 8发布动态 9发布节目 10一健打招呼钻石支付 11解锁社交账号
     * @param payType 1/余额支付 2/google支付 3/my_card支付 4/苹果支付
     * @return
     */
    @FormUrlEncoded
    @POST("api/order")
    Observable<BaseDataResponse<CreateOrderEntity>> createOrder(
            @Field("id") Integer id,
            @Field("type") Integer type,
            @Field("pay_type") Integer payType,
            @Field("toUserId") Integer toUserId
    );

    /**
     * 币支付订单
     *
     * @param orderNumber
     * @return
     */
    @FormUrlEncoded
    @POST("api/order/balancePay")
    Observable<BaseResponse> coinPayOrder(
            @Field("order_number") String orderNumber
    );

    /**
     * 动态点赞详情
     *
     * @param id 动态ID
     * @return
     */
    @GET("api/news/give/{id}")
    Observable<BaseListDataResponse<BaseUserBeanEntity>> getNewsGiveList(
            @Path("id") Integer id,
            @Query("page") Integer page
    );

    /**
     * 节目点赞详情
     *
     * @param id 节目ID
     * @return
     */
    @GET("api/topical/give/{id}")
    Observable<BaseListDataResponse<BaseUserBeanEntity>> getTopicalGiveList(
            @Path("id") Integer id,
            @Query("page") Integer page
    );

    /**
     * 是否可以发布节目
     *
     * @return
     */
    @GET("api/topical/check")
    Observable<BaseResponse> checkTopical();

    /**
     * 举报节目报名人
     *
     * @param id 报名ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/topical/signUpReport")
    Observable<BaseResponse> signUpReport(
            @Field("id") Integer id);

    /**
     * 发送钻石红包
     *
     * @param userId
     * @param money
     * @return
     */
    @FormUrlEncoded
    @POST("api/redPackage/send")
    Observable<BaseDataResponse<ChatRedPackageEntity>> sendCoinRedPackage(
            @Field("user_id") Integer userId,
            @Field("money") Integer money,
            @Field("desc") String desc
    );

    /**
     * 红包详情
     *
     * @param id
     * @return
     */
    @GET("api/redPackage/{id}")
    Observable<BaseDataResponse<ChatRedPackageEntity>> getCoinRedPackage(
            @Path("id") int id
    );

    /**
     * 领取红包
     *
     * @param id
     * @return
     */
    @GET("api/redPackage/receive")
    Observable<BaseResponse> receiveCoinRedPackage(
            @Query("id") int id
    );

    /**
     * 用户币收益记录
     *
     * @param page
     * @return
     */
    @GET("api/userCoin")
    Observable<BaseListDataResponse<UserCoinItemEntity>> userCoinEarnings(
            @Query("page") int page
    );

    /**
     * 用户是否允许连麦
     *
     * @param userId
     * @return
     */
    @GET("api/user/isConnection")
    Observable<BaseDataResponse<UserConnMicStatusEntity>> userIsConnMic(
            @Query("user_id") int userId
    );

    /**
     * 支付成功回调
     *
     * @param packageName
     * @param orderNumber
     * @param productId
     * @param token
     * @param type        1普通支付 2订阅支付
     * @param event       事件回调（通常在谷歌支付成功或者异常情况下上报）
     * @return
     */
    @FormUrlEncoded
    @POST("api/order/googleNotify")
    Observable<BaseResponse> paySuccessNotify(
            @Field("package_name") String packageName,
            @Field("order_number") String orderNumber,
            @Field("product_id[]") List<String> productId,
            @Field("token") String token,
            @Field("type") int type,
            @Field("event") Integer event
    );

    /**
     * 更新设备推送ID
     *
     * @param deviceId
     * @return
     */
    @FormUrlEncoded
    @POST("api/device")
    Observable<BaseResponse> pushDeviceToken(
            @Field("device_id") String deviceId,
            @Field("version_number") String version_number
    );


    /**
     * 回复相册申请
     *
     * @param applyId
     * @param status
     * @return
     */
    @FormUrlEncoded
    @POST("api/user/verify-status")
    Observable<BaseResponse> replyApplyAlubm(
            @Field("apply_id") int applyId,
            @Field("status") int status
    );

    /**
     * 查看申请消息照片
     *
     * @return
     */
    @GET("api/user/verify")
    Observable<BaseResponse> checkApplyAlbumPhoto(
            @Query("apply_id") int applyId
    );

    /**
     * 电台&節目发布检测
     *
     * @param type 1动态 2节目
     * @return
     */
    @FormUrlEncoded
    @POST("api/news/publishCheck")
    Observable<BaseDataResponse<StatusEntity>> publishCheck(
            @Field("type") int type
    );

    @GET("api/message/unread")
    Observable<BaseDataResponse<UnReadMessageNumEntity>> getUnreadMessageNum();


    /**
     * 游戏币兑换jm币对话框信息
     *
     * @return
     */
    @GET("calling/userAccount/getCoinExchangeBoxInfo")
    Observable<BaseDataResponse<CoinExchangeBoxInfo>> getCoinExchangeBoxInfo();

    /**
     * 游戏币兑换jm币对话框信息
     *
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("calling/userAccount/exchangeCoins")
    Observable<BaseResponse> exchangeCoins(@Body RequestBody requestBody);

    /**
     * 游戏币充值列表
     *
     * @param type 类型 vip:升级会员 recharge:充值
     * @return
     */
    @GET("api/goods")
    Observable<BaseDataResponse<List<GameCoinBuy>>> buyGameCoins(
            @Query("type") String type);
}
