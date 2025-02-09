package com.dl.playfun.data.source.http;

import com.blankj.utilcode.util.GsonUtils;
import com.dl.playfun.data.source.HttpDataSource;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseListDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.data.source.http.service.ApiService;
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
import com.dl.playfun.utils.ApiUitl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;

/**
 * @author goldze
 * @date 2019/3/26
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private volatile static HttpDataSourceImpl INSTANCE = null;
    private final ApiService apiService;

    private HttpDataSourceImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    public static HttpDataSourceImpl getInstance(ApiService apiService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<BaseDataResponse<LevelApiEntity>> adjustLevelPrice(RequestBody requestBody) {
        return apiService.adjustLevelPrice(requestBody);
    }

    @Override
    public Observable<BaseDataResponse<LevelPageInfoEntity>> getUserLevelPageInfo() {
        return apiService.getUserLevelPageInfo();
    }

    @Override
    public Observable<BaseDataResponse<IMTransUserEntity>> transUserIM(String IMUserId) {
        return apiService.transUserIM(IMUserId);
    }

    @Override
    public Observable<BaseDataResponse<ChatDetailCoinEntity>> getTotalCoins(Integer dismissRoom) {
        return apiService.getTotalCoins(dismissRoom);
    }

    @Override
    public Observable<BaseResponse> GamePaySuccessNotify(String packageName, String orderNumber, List<String> productId, String token, int type, Integer event, String serverId, String roleId) {
        return apiService.GamePaySuccessNotify(packageName, orderNumber, productId,token,type,event,serverId,roleId);
    }

    @Override
    public Observable<BaseDataResponse<GamePhotoAlbumEntity>> getGamePhotoAlbumList(String serverId, String roleId) {
        return apiService.getGamePhotoAlbumList(serverId, roleId);
    }

    @Override
    public Observable<BaseResponse> setGameState(int gameState) {
        return apiService.setGameState(gameState);
    }

    @Override
    public Observable<BaseResponse> commitRoleInfo(RequestBody requestBody) {
        return apiService.commitRoleInfo(requestBody);
    }

    @Override
    public Observable<BaseResponse> upUserSex(Integer sex) {
        return apiService.upUserSex(sex);
    }

    @Override
    public Observable<BaseDataResponse<CreateOrderEntity>> createChatDetailOrder(Integer id, Integer type, Integer payType, Integer toUserId, Integer channel) {
        return apiService.createChatDetailOrder(id, type, payType, toUserId, channel);
    }

    @Override
    public Observable<BaseDataResponse<PriceConfigEntity.Current>> getMaleRefundMsg(Integer toUserId, Integer type) {
        return apiService.getMaleRefundMsg(toUserId, type);
    }

    @Override
    public Observable<BaseDataResponse> getTips(Integer toUserId, Integer type, String isShow) {
        return apiService.getTips(toUserId, type, isShow);
    }

    @Override
    public Observable<BaseResponse> addIMCollect(Integer userId, Integer type) {
        return apiService.addIMCollect(userId, type);
    }

    @Override
    public Observable<BaseDataResponse<Map<String, Integer>>> verifyGoddessTips(Integer toUserId) {
        return apiService.verifyGoddessTips(toUserId);
    }

    @Override
    public Observable<BaseDataResponse<PriceConfigEntity>> getPriceConfig(Integer to_user_id) {
        return apiService.getPriceConfig(to_user_id);
    }

    @Override
    public Observable<BaseDataResponse<CallingInfoEntity.SayHiList>> getSayHiList(Integer page, Integer perPage) {
        return apiService.getSayHiList(page, perPage);
    }

    @Override
    public Observable<BaseDataResponse<CallingInfoEntity>> getCallingInfo(Integer roomId, Integer callingType, String fromUserId, String toUserId) {
        return apiService.getCallingInfo(roomId, callingType, fromUserId, toUserId);
    }

    @Override
    public Observable<BaseDataResponse<CallingInviteInfo>> callingInviteInfo(Integer callingType, String fromUserId, String toUserId) {
        return apiService.callingInviteInfo(callingType, fromUserId, toUserId);
    }

    @Override
    public Observable<BaseResponse> sendUserGift(RequestBody requestBody) {
        return apiService.sendUserGift(gift_id, to_user_id, amount,type);
    }

    @Override
    public Observable<BaseDataResponse<GiftBagEntity>> getBagGiftInfo() {
        return apiService.getBagGiftInfo();
    }

    @Override
    public Observable<BaseDataResponse<UserProfitPageEntity>> getUserProfitPageInfo(Long currentUserId, Integer page, Integer perPage) {
        return apiService.getUserProfitPageInfo(currentUserId, page, perPage);
    }

    @Override
    public Observable<BaseDataResponse<CoinWalletEntity>> getUserAccount() {
        return apiService.getUserAccount();
    }

    @Override
    public Observable<BaseDataResponse<GameCoinWalletEntity>> getUserAccountPageInfo() {
        return apiService.getUserAccountPageInfo();
    }

    @Override
    public Observable<BaseDataResponse<BubbleEntity>> getBubbleEntity() {
        return apiService.getBubbleEntity();
    }

    @Override
    public Observable<BaseDataResponse<AccostEntity>> getAccostList(Integer page) {
        return apiService.getAccostList(page);
    }

    @Override
    public Observable<BaseResponse> putAccostList(List<Integer> userIds) {
        return apiService.putAccostList(userIds);
    }

    @Override
    public Observable<BaseResponse> putAccostFirst(Integer userId) {
        return apiService.putAccostFirst(userId);
    }

    @Override
    public Observable<BaseDataResponse<BroadcastListEntity>> getBroadcastHome(Integer sex, Integer city_id, Integer game_id, Integer is_online, Integer is_collect, Integer type, Integer page) {
        return apiService.getBroadcastHome(sex, city_id, game_id, is_online, is_collect, type, page);
    }

    @Override
    public Observable<BaseDataResponse<List<MessageRuleEntity>>> getMessageRule() {
        return apiService.getMessageRule();
    }

    @Override
    public Observable<BaseDataResponse> getSensitiveWords() {
        return apiService.getSensitiveWords();
    }

    @Override
    public Observable<BaseDataResponse<PhotoAlbumEntity>> getPhotoAlbum(Integer user_id) {
        return apiService.getPhotoAlbum(user_id);
    }

    @Override
    public Observable<BaseResponse> removeUserSound() {
        return apiService.removeUserSound();
    }

    @Override
    public Observable<BaseDataResponse> putUserSound(String paht, Integer sound_time) {
        return apiService.putUserSound(paht, sound_time);
    }

    @Override
    public Observable<BaseListDataResponse<SoundEntity>> getUserSound(Integer page) {
        return apiService.getUserSound(page);
    }

    @Override
    public Observable<BaseResponse> topicalCreateMood(String describe, String start_date, List<String> images, Integer is_comment, Integer is_hide, Double longitude, Double latitude, String video, Integer news_type) {
        return apiService.topicalCreateMood(describe, start_date, images, is_comment, is_hide, longitude, latitude, video, news_type);
    }

    @Override
    public Observable<BaseListDataResponse<BroadcastEntity>> broadcastAll(Integer page) {
        return apiService.broadcastAll(page);
    }

    @Override
    public Observable<BaseDataResponse<List<GoodsEntity>>> pointsGoodList() {
        return apiService.pointsGoodList("points");
    }

    @Override
    public Observable<BaseResponse> pushGreet(Integer type) {
        return apiService.pushGreet(type);
    }

    @Override
    public Observable<BaseDataResponse<Map<String, String>>> isBlacklist(String userId) {
        return apiService.isBlacklist(userId);
    }

    @Override
    public Observable<BaseListDataResponse<TaskAdEntity>> rechargeVipList() {
        return apiService.rechargeVipList();
    }

    @Override
    public Observable<BaseDataResponse<Map<String, String>>> isOnlineUser(String userId) {
        return apiService.isOnlineUser(userId);
    }


    @Override
    public Observable<BaseDataResponse<BrowseNumberEntity>> newsBrowseNumber() {
        return apiService.newsBrowseNumber();
    }

    @Override
    public Observable<BaseListDataResponse<TraceEntity>> toBrowse(Integer page) {
        return apiService.toBrowse(page);
    }

    @Override
    public Observable<BaseListDataResponse<TraceEntity>> collectFans(Integer page) {
        return apiService.collectFans(page);
    }

    @Override
    public Observable<BaseListDataResponse<TraceEntity>> collect(Integer page) {
        return apiService.collect(page);
    }

    @Override
    public Observable<BaseResponse> reportUserLocation(String latitude, String longitude) {
        return apiService.reportUserLocation(latitude, longitude);
    }

    @Override
    public Observable<BaseResponse> repoetLocalGoogleOrder(Map<String, Object> map) {
        return apiService.repoetLocalGoogleOrder(map);
    }

    @Override
    public Observable<BaseDataResponse<CreateOrderEntity>> createOrderUserDetail(Integer id, Integer type, Integer payType, Integer number) {
        return apiService.createOrderUserDetail(id, type, payType, number);
    }

    @Override
    public Observable<BaseDataResponse<Map<String, String>>> freeSevenDay(Integer pay_type, Integer goods_type) {
        return apiService.freeSevenDay(pay_type, goods_type);
    }

    @Override
    public Observable<BaseDataResponse<TagEntity>> tag(String to_user_id) {
        return apiService.tag(to_user_id);
    }

    @Override
    public Observable<BaseResponse> userInvite(String code, Integer type, String channel) {
        return apiService.userInvite(code, type, channel);
    }

    @Override
    public Observable<BaseResponse> isBindCity(Integer city_id) {
        return apiService.isBindCity(city_id);
    }

    @Override
    public Observable<BaseDataResponse<UserDataEntity>> regUser(String nickname, String avatar, String birthday, Integer sex) {
        return apiService.regUser(nickname, avatar, birthday, sex);
    }

    @Override
    public Observable<BaseResponse> coordinate(Double latitude, Double longitude, String county_name, String province_name) {
        return apiService.coordinate(latitude, longitude, county_name, province_name);
    }

    @Override
    public Observable<BaseDataResponse<SwiftMessageEntity>> getSwiftMessage(Integer page) {
        return apiService.getSwiftMessage(page);
    }

    @Override
    public Observable<BaseResponse> bindAccount(String id, String type) {
        return apiService.bindAccount(id, type);
    }

    @Override
    public Observable<BaseDataResponse<UserDataEntity>> v2Login(String phone, String code, String device_code) {
        return apiService.v2Login(phone, code, device_code);
    }

    @Override
    public Observable<BaseDataResponse<Map<String, String>>> imagFaceUpload(String imgUrl) {
        return apiService.imagFaceUpload(imgUrl);
    }

    @Override
    public Observable<BaseDataResponse<VersionEntity>> detectionVersion(String client) {
        return apiService.detectionVersion(client);
    }

    @Override
    public Observable<BaseDataResponse<GoogleNearPoiBean>> nearSearchPlace(RequestBody requestBody) {
        return apiService.nearSearchPlace(requestBody);
    }

    @Override
    public Observable<BaseDataResponse<GooglePoiBean>> textSearchPlace(RequestBody requestBody) {
        return apiService.textSearchPlace(requestBody);
    }

    @Override
    public Observable<BaseResponse> verifyCodePost(String phone) {
        return apiService.verifyCodePost(phone);
    }


    /**
     * 注册
     *
     * @param phone    邮箱
     * @param password 密码
     * @return
     */
    @Override
    public Observable<BaseDataResponse<TokenEntity>> register(String phone, String password, String code) {
        return apiService.registerPost(phone, password, code);
    }

    /**
     * 同意用户协议
     *
     * @return
     */
    @Override
    public Observable<BaseResponse> acceptUseAgreement() {
        return apiService.acceptUseAgreement(1);
    }

    /**
     * 注册
     *
     * @param phone    手机号
     * @param password 密码
     * @return
     */
    @Override
    public Observable<BaseDataResponse<TokenEntity>> login(String phone, String password) {
        return apiService.loginPost(phone, password);
    }

    @Override
    public Observable<BaseDataResponse<UserDataEntity>> authLoginPost(String id, String type) {
        return apiService.authLoginPost(id, type);
    }

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
    @Override
    public Observable<BaseListDataResponse<ParkItemEntity>> homeList(
            Integer cityId,
            Integer type,
            Integer isOnline,
            Integer sex,
            String searchName,
            Double longitude,
            Double latitude,
            Integer page) {
        return apiService.homeListGet(cityId, type, isOnline, sex, searchName, longitude, latitude, page);
    }

    @Override
    public Observable<BaseDataResponse<UserInfoEntity>> getUserInfo() {
        return apiService.getUserInfo(1);
    }

    /**
     * 个人资料
     *
     * @return
     */
    @Override
    public Observable<BaseDataResponse<UserDataEntity>> getUserData() {
        return apiService.getUserData(1);
    }

    /**
     * 游戏地区
     *
     * @return
     */
    @Override
    public Observable<BaseDataResponse<List<RadioTwoFilterItemEntity>>> getGameCity() {
        return apiService.getGameCity("gameCity");
    }


    @Override
    public Observable<BaseResponse> userRemark(Integer userId, String nickname, String remark) {
        return apiService.userRemark(userId, nickname, remark);
    }

    @Override
    public Observable<BaseDataResponse<UserRemarkEntity>> getUserRemark(Integer userId) {
        return apiService.getUserRemark(userId);
    }

    @Override
    public Observable<BaseResponse> updateAvatar(String avatar) {
        return apiService.updateAvatar(avatar);
    }


    @Override
    public Observable<BaseResponse> updateUserData(String nickname, List<Integer> permanent_city_ids, String birthday, String occupation, List<Integer> program_ids, List<Integer> hope_object_ids, String facebook, String insgram,Integer accountType, Integer is_weixin_show, Integer height, Integer weight, String desc) {
        return apiService.updateUserData(nickname, permanent_city_ids, birthday, occupation, program_ids, hope_object_ids, facebook, insgram,accountType, is_weixin_show, height, weight, desc);
    }

    @Override
    public Observable<BaseDataResponse<UserDetailEntity>> userMain(Integer id, Double longitude, Double latitude) {
        return apiService.userMain(id, longitude, latitude);
    }

    @Override
    public Observable<BaseResponse> topicalCreate(Integer theme_id, String describe, String address, List<Integer> hope_object, String start_date, Integer end_time, List<String> images, Integer is_comment, Integer is_hide, String address_name, Integer city_id, Double longitude, Double latitude, String video) {
        return apiService.topicalCreate(theme_id, describe, address, hope_object, start_date, end_time, images, is_comment, is_hide, address_name, city_id, longitude, latitude, video);
    }

    @Override
    public Observable<BaseResponse> singUp(Integer id, String img) {
        return apiService.singUp(id, img);
    }

    @Override
    public Observable<BaseListDataResponse<BlackEntity>> getBlackList(Integer page) {
        return apiService.getBlackList(page);
    }

    @Override
    public Observable<BaseResponse> addBlack(Integer user_id) {
        return apiService.addBlack(user_id);
    }

    @Override
    public Observable<BaseResponse> deleteBlack(Integer id) {
        return apiService.deleteBlack(id);
    }

    @Override
    public Observable<BaseResponse> deleteTopical(Integer id) {
        return apiService.deleteTopical(id);
    }

    @Override
    public Observable<BaseListDataResponse<ParkItemEntity>> getCollectList(int page, Double latitude, Double longitude) {
        return apiService.getCollectList(page, latitude, longitude);
    }

    @Override
    public Observable<BaseResponse> addCollect(Integer userId) {
        return apiService.addCollect(userId);
    }

    @Override
    public Observable<BaseResponse> deleteCollect(Integer userId) {
        return apiService.deleteCollect(userId);
    }

    @Override
    public Observable<BaseResponse> newsCreate(String content, List<String> images, Integer is_comment, Integer is_hide) {
        return apiService.newsCreate(content, images, is_comment, is_hide);
    }

    @Override
    public Observable<BaseDataResponse<NewsEntity>> newsDetail(Integer id) {
        return apiService.newsDetail(id);
    }

    @Override
    public Observable<BaseResponse> deleteNews(Integer id) {
        return apiService.deleteNews(id);
    }

    @Override
    public Observable<BaseListDataResponse<BroadcastEntity>> broadcast(Integer type, Integer theme_id, Integer is_online, Integer city_id, Integer sex, Integer page) {
        return apiService.broadcast(type, theme_id, is_online, city_id, sex, page);
    }

    @Override
    public Observable<BaseListDataResponse<NewsEntity>> getNewsList(Integer user_id, Integer page) {
        return apiService.getNewsList(user_id, page);
    }


    @Override
    public Observable<BaseListDataResponse<TopicalListEntity>> getTopicalList(Integer userId, Integer page) {
        return apiService.getTopicalList(userId, page);
    }

    @Override
    public Observable<BaseResponse> report(Integer id, String type, String reasonId, List<String> images, String desc) {
        return apiService.report(id, type, reasonId, images, desc);
    }

    @Override
    public Observable<BaseResponse> topicalComment(Integer id, String content, Integer to_user_id) {
        return apiService.topicalComment(id, content, to_user_id);
    }

    @Override
    public Observable<BaseResponse> newsComment(Integer id, String content, Integer to_user_id) {
        return apiService.newsComment(id, content, to_user_id);
    }

    @Override
    public Observable<BaseDataResponse<StatusEntity>> evaluateStatus(Integer userId) {
        return apiService.evaluateStatus(userId);
    }

    @Override
    public Observable<BaseResponse> evaluateCreate(Integer userId, Integer tagId, String img) {
        return apiService.evaluateCreate(userId, tagId, img);
    }

    @Override
    public Observable<BaseDataResponse<List<EvaluateEntity>>> evaluate(Integer userId) {
        return apiService.evaluate(userId);
    }

    @Override
    public Observable<BaseResponse> newsGive(Integer id) {
        return apiService.newsGive(id);
    }

    @Override
    public Observable<BaseDataResponse<IsChatEntity>> isChat(Integer userId) {
        return apiService.isChat(userId);
    }

    @Override
    public Observable<BaseResponse> useVipChat(Integer userId, Integer type) {
        return apiService.useVipChat(userId, type);
    }

    @Override
    public Observable<BaseResponse> imgeReadLog(Integer image_id) {
        return apiService.imgeReadLog(image_id);
    }

    @Override
    public Observable<BaseResponse> password(String original_password, String new_password) {
        return apiService.password(original_password, new_password);
    }

    @Override
    public Observable<BaseDataResponse<AllConfigEntity>> getAllConfig() {
        return apiService.getAllConfig();
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getProgramTimeConfig() {
        return apiService.getSystemConfig("program_time");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getHeightConfig() {
        return apiService.getSystemConfig("height");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getWeightConfig() {
        return apiService.getSystemConfig("weight");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getReportReasonConfig() {
        return apiService.getSystemConfig("report_reason");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getEvaluateConfig() {
        return apiService.getSystemConfig("evaluate");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getHopeObjectConfig() {
        return apiService.getSystemConfig("hope_object");
    }

    @Override
    public Observable<BaseDataResponse<List<OccupationConfigItemEntity>>> getOccupationConfig() {
        return apiService.getOccupationConfig("occupation");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getThemeConfig() {
        return apiService.getThemeConfig("theme");
    }

    @Override
    public Observable<BaseDataResponse<List<ConfigItemEntity>>> getCityConfig() {
        return apiService.getSystemConfig("city");
    }

    @Override
    public Observable<BaseResponse> userVerify(Integer user_id, String img) {
        return apiService.userVerify(user_id, img);
    }

    @Override
    public Observable<BaseDataResponse<List<GoodsEntity>>> goods() {
        return apiService.goods("recharge");
    }

    @Override
    public Observable<BaseDataResponse<List<VipPackageItemEntity>>> vipPackages() {
        return apiService.vipPackages("vip");
    }

    @Override
    public Observable<BaseDataResponse> saveVerifyFaceImage(String imgPath) {
        return apiService.saveVerifyFaceImage(imgPath);
    }

    @Override
    public Observable<BaseResponse> cashOut(float money) {
        return apiService.cashOut(money);
    }

    @Override
    public Observable<BaseResponse> sendCcode(String phone) {
        return apiService.sendCcode(phone);
    }

    @Override
    public Observable<BaseListDataResponse<AlbumPhotoEntity>> albumImage(Integer userId, Integer type) {
        return apiService.albumImage(userId, type);
    }

    @Override
    public Observable<BaseResponse> albumInsert(Integer fileType, String src, Integer isBurn, String videoImage) {
        return apiService.albumInsert(fileType, src, isBurn, videoImage);
    }

    @Override
    public Observable<BaseDataResponse<List<AlbumPhotoEntity>>> delAlbumImage(Integer id) {
        return apiService.delAlbumImage(id);
    }

    @Override
    public Observable<BaseResponse> setBurnAlbumImage(Integer imgId, Boolean state) {
        return apiService.setAlbumImage(imgId, 1, state ? 1 : 0);
    }

    @Override
    public Observable<BaseResponse> setRedPackageAlbumImage(Integer imgId, Boolean state) {
        return apiService.setAlbumImage(imgId, 2, state ? 1 : 0);
    }

    @Override
    public Observable<BaseResponse> setRedPackageAlbumVideo(Integer videoId, Boolean state) {
        return apiService.setAlbumImage(videoId, 3, state ? 1 : 0);
    }

    @Override
    public Observable<BaseDataResponse<FaceVerifyResultEntity>> faceVerifyResult(String bizId) {
        return apiService.faceVerifyResult(bizId);
    }

    @Override
    public Observable<BaseDataResponse<StatusEntity>> faceIsCertification() {
        return apiService.faceIsCertification();
    }

    @Override
    public Observable<BaseDataResponse<PrivacyEntity>> getPrivacy() {
        return apiService.getPrivacy();
    }

    @Override
    public Observable<BaseResponse> setPrivacy(PrivacyEntity privacyEntity) {
        return apiService.setPrivacy(
                privacyEntity.getHome() == null ? null : privacyEntity.getHome() ? 1 : 0,
                privacyEntity.getDistance() == null ? null : privacyEntity.getDistance() ? 1 : 0,
                privacyEntity.getOnlineIme() == null ? null : privacyEntity.getOnlineIme() ? 1 : 0,
                privacyEntity.getConnection() == null ? null : privacyEntity.getConnection() ? 1 : 0,
                privacyEntity.getNearby() == null ? null : privacyEntity.getNearby() ? 1 : 0
        );
    }

    @Override
    public Observable<BaseResponse> updatePhone(String phone, int code, String password) {
        return apiService.updatePhone(phone, code, password);
    }

    @Override
    public Observable<BaseResponse> applyGoddess(List<String> images) {
        return apiService.applyGoddess(images);
    }

    @Override
    public Observable<BaseDataResponse<StatusEntity>> applyGoddessResult() {
        return apiService.applyGoddessResult();
    }

    @Override
    public Observable<BaseResponse> resetPassword(String phone, int code, String password) {
        return apiService.resetPassword(phone, code, password);
    }

    @Override
    public Observable<BaseResponse> setSex(int sex) {
        return apiService.setSex(sex);
    }

    @Override
    public Observable<BaseDataResponse<CashWalletEntity>> cashWallet() {
        return apiService.cashWallet();
    }

    @Override
    public Observable<BaseDataResponse<CoinWalletEntity>> coinWallet() {
        return apiService.coinWallet();
    }

    @Override
    public Observable<BaseResponse> setWithdrawAccount(String realName, String account) {
        return apiService.setWithdrawAccount(realName, account);
    }

    @Override
    public Observable<BaseResponse> setAlbumPrivacy(Integer type, Integer money) {
        return apiService.setAlbumPrivacy(type, money);
    }

    @Override
    public Observable<BaseResponse> setComment(Integer id, Integer isComment) {
        return apiService.setComment(id, isComment);
    }

    @Override
    public Observable<BaseListDataResponse<ApplyMessageEntity>> getMessageApply(Integer page) {
        return apiService.getMessageApply(page);
    }

    @Override
    public Observable<BaseListDataResponse<BoradCastMessageEntity>> getMessageBoradcast(Integer page) {
        return apiService.getMessageBoradcast(page);
    }

    @Override
    public Observable<BaseListDataResponse<CommentMessageEntity>> getMessageComment(Integer page) {
        return apiService.getMessageComment(page);
    }

    @Override
    public Observable<BaseListDataResponse<EvaluateMessageEntity>> getMessageEvaluate(Integer page) {
        return apiService.getMessageEvaluate(page);
    }

    @Override
    public Observable<BaseListDataResponse<GiveMessageEntity>> getMessageGive(Integer page) {
        return apiService.getMessageGive(page);
    }

    @Override
    public Observable<BaseListDataResponse<SignMessageEntity>> getMessageSign(Integer page) {
        return apiService.getMessageSign(page);
    }

    @Override
    public Observable<BaseListDataResponse<ProfitMessageEntity>> getMessageProfit(Integer page) {
        return apiService.getMessageProfit(page);
    }

    @Override
    public Observable<BaseResponse> evaluateAppeal(Integer messageId, Integer tagId) {
        return apiService.evaluateAppeal(messageId, tagId);
    }

    @Override
    public Observable<BaseListDataResponse<SystemMessageEntity>> getMessageSystem(Integer page) {
        return apiService.getMessageSystem(page);
    }

    @Override
    public Observable<BaseResponse> deleteMessage(String type, Integer id) {
        if ("system".equals(type)) {
            return apiService.deleteMessageSystem(id);
        } else if ("apply".equals(type)) {
            return apiService.deleteMessageApply(id);
        } else if ("broadcast".equals(type)) {
            return apiService.deleteMessageBroadcast(id);
        } else if ("comment".equals(type)) {
            return apiService.deleteMessageComment(id);
        } else if ("sign".equals(type)) {
            return apiService.deleteMessageSign(id);
        } else if ("give".equals(type)) {
            return apiService.deleteMessageGive(id);
        } else if ("evaluate".equals(type)) {
            return apiService.deleteMessageEvaluate(id);
        } else if ("profit".equals(type)) {
            return apiService.deleteMessageProfit(id);
        }
        throw new IllegalArgumentException("Unknown type : " + type);
    }

    @Override
    public Observable<BaseDataResponse<List<MessageGroupEntity>>> getMessageList() {
        return apiService.getMessageList();
    }

    @Override
    public Observable<BaseDataResponse<PushSettingEntity>> getPushSetting() {
        return apiService.getPushSetting();
    }

    @Override
    public Observable<BaseResponse> savePushSetting(PushSettingEntity pushSettingEntity) {
        return apiService.savePushSetting(
                pushSettingEntity.getChat() == null ? null : pushSettingEntity.getChat() ? 1 : 0,
                pushSettingEntity.getSign() == null ? null : pushSettingEntity.getSign() ? 1 : 0,
                pushSettingEntity.getGive() == null ? null : pushSettingEntity.getGive() ? 1 : 0,
                pushSettingEntity.getComment() == null ? null : pushSettingEntity.getComment() ? 1 : 0,
                pushSettingEntity.getBroadcast() == null ? null : pushSettingEntity.getBroadcast() ? 1 : 0,
                pushSettingEntity.getApply() == null ? null : pushSettingEntity.getApply() ? 1 : 0,
                pushSettingEntity.getInvitation() == null ? null : pushSettingEntity.getInvitation() ? 1 : 0,
                pushSettingEntity.getSound() == null ? null : pushSettingEntity.getSound() ? 1 : 0,
                pushSettingEntity.getShake() == null ? null : pushSettingEntity.getShake() ? 1 : 0
        );
    }

    @Override
    public Observable<BaseResponse> TopicalGive(Integer id) {
        return apiService.TopicalGive(id);
    }

    @Override
    public Observable<BaseResponse> TopicalFinish(Integer id) {
        return apiService.TopicalFinish(id);
    }

    @Override
    public Observable<BaseResponse> burnReset() {
        return apiService.burnReset();
    }

    @Override
    public Observable<BaseDataResponse<CreateOrderEntity>> createOrder(Integer id, Integer type, Integer payType, Integer toUserId) {
        return apiService.createOrder(id, type, payType, toUserId);
    }

    @Override
    public Observable<BaseResponse> coinPayOrder(String orderNumber) {
        return apiService.coinPayOrder(orderNumber);
    }

    @Override
    public Observable<BaseListDataResponse<BaseUserBeanEntity>> getNewsGiveList(Integer id, Integer page) {
        return apiService.getNewsGiveList(id, page);
    }

    @Override
    public Observable<BaseListDataResponse<BaseUserBeanEntity>> getTopicalGiveList(Integer id, Integer page) {
        return apiService.getTopicalGiveList(id, page);
    }

    @Override
    public Observable<BaseDataResponse<TopicalListEntity>> topicalDetail(Integer id) {
        return apiService.topicalDetail(id);
    }

    @Override
    public Observable<BaseResponse> checkTopical() {
        return apiService.checkTopical();
    }

    @Override
    public Observable<BaseResponse> signUpReport(Integer id) {
        return apiService.signUpReport(id);
    }

    @Override
    public Observable<BaseDataResponse<ChatRedPackageEntity>> sendCoinRedPackage(Integer userId, Integer money, String desc) {
        return apiService.sendCoinRedPackage(userId, money, desc);
    }

    @Override
    public Observable<BaseDataResponse<ChatRedPackageEntity>> getCoinRedPackage(int id) {
        return apiService.getCoinRedPackage(id);
    }

    @Override
    public Observable<BaseResponse> receiveCoinRedPackage(int id) {
        return apiService.receiveCoinRedPackage(id);
    }

    @Override
    public Observable<BaseListDataResponse<UserCoinItemEntity>> userCoinEarnings(int page) {
        return apiService.userCoinEarnings(page);
    }

    @Override
    public Observable<BaseDataResponse<UserConnMicStatusEntity>> userIsConnMic(int userId) {
        return apiService.userIsConnMic(userId);
    }

    @Override
    public Observable<BaseResponse> paySuccessNotify(String packageName, String orderNumber, List<String> productId, String token, int type, Integer event) {
        return apiService.paySuccessNotify(packageName, orderNumber, productId, token, type, event);
    }

    @Override
    public Observable<BaseResponse> pushDeviceToken(String deviceId, String version_number) {
        return apiService.pushDeviceToken(deviceId, version_number);
    }

    @Override
    public Observable<BaseResponse> replyApplyAlubm(int applyId, boolean status) {
        return apiService.replyApplyAlubm(applyId, status ? 1 : 2);
    }

    @Override
    public Observable<BaseResponse> checkApplyAlbumPhoto(int applyId) {
        return apiService.checkApplyAlbumPhoto(applyId);
    }

    @Override
    public Observable<BaseDataResponse<StatusEntity>> publishCheck(int type) {
        return apiService.publishCheck(type);
    }

    @Override
    public Observable<BaseDataResponse<UnReadMessageNumEntity>> getUnreadMessageNum() {
        return apiService.getUnreadMessageNum();
    }

    @Override
    public Observable<BaseDataResponse<CoinExchangeBoxInfo>> getCoinExchangeBoxInfo() {
        return apiService.getCoinExchangeBoxInfo();
    }

    @Override
    public Observable<BaseResponse> exchangeCoins(int id) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", id);
        return apiService.exchangeCoins(ApiUitl.getBody(GsonUtils.toJson(map)));
    }

    @Override
    public Observable<BaseDataResponse<List<GameCoinBuy>>> buyGameCoins() {
        return apiService.buyGameCoins("recharge");
    }

    @Override
    public Observable<BaseResponse> sendEmailCode(String email) {
        return apiService.sendEmailCode(email);
    }

    @Override
    public Observable<BaseResponse> bindUserEmail(String email, String code, String pass, Integer type) {
        return apiService.bindUserEmail(email, code, pass, type);
    }

    @Override
    public Observable<BaseDataResponse<UserDataEntity>> loginEmail(String email, String code, Integer type) {
        return apiService.loginEmail(email, code, type);
    }

    @Override
    public Observable<BaseDataResponse<CallingStatusEntity>> getCallingStatus(Integer roomId) {
        return apiService.getCallingStatus(roomId);
    }

    @Override
    public Observable<BaseDataResponse<CallingStatusEntity>> getRoomStatus(Integer roomId) {
        return apiService.getRoomStatus(roomId);
    }
}
