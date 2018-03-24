package ru.todo100.activer.populators;

import org.springframework.beans.factory.annotation.Autowired;
import ru.todo100.activer.dao.AccountGiftDao;
import ru.todo100.activer.dao.BalanceDao;
import ru.todo100.activer.dao.PhotoDao;
import ru.todo100.activer.dao.SettingDao;
import ru.todo100.activer.data.*;
import ru.todo100.activer.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */
public class ProfilePopulator implements Populator<AccountItem, ProfileData> {
    private PhotoDao photoService;
    private BalanceDao balanceDao;
    private EducationPopulator educationPopulator;
    private JobPopulator jobPopulator;
    private ChildrenPopulator childrenPopulator;
    private InterestPopulator interestPopulator;
    private TripPopulator tripPopulator;
    private DreamPopulator dreamPopulator;
    private VideoPopulator videoPopulator;
    private SettingDao settingService;
    private AccountGiftDao accountGiftDao;
    private AccountGiftDataPopulator accountGiftDataPopulator;

    private static Integer calculateAge(final Calendar dob) {
        if (dob == null) return 0;
        final Calendar dayOfBirth = (Calendar) dob.clone();

        final Calendar today = Calendar.getInstance();
        // include day of birth
        dayOfBirth.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - dayOfBirth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dayOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private PhotoDao getPhotoService() {
        return photoService;
    }

    @Autowired
    public void setPhotoService(PhotoDao photoService) {
        this.photoService = photoService;
    }

    private SettingDao getSettingService() {
        return settingService;
    }

    @Autowired
    public void setSettingService(SettingDao settingService) {
        this.settingService = settingService;
    }

    private AccountGiftDao getAccountGiftDao() {
        return accountGiftDao;
    }

    @Autowired
    public void setAccountGiftDao(AccountGiftDao accountGiftDao) {
        this.accountGiftDao = accountGiftDao;
    }

    private VideoPopulator getVideoPopulator() {
        return videoPopulator;
    }

    @Autowired
    public void setVideoPopulator(VideoPopulator videoPopulator) {
        this.videoPopulator = videoPopulator;
    }

    private BalanceDao getBalanceDao() {
        return balanceDao;
    }

    @Autowired
    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    private DreamPopulator getDreamPopulator() {
        return dreamPopulator;
    }

    @Autowired
    public void setDreamPopulator(DreamPopulator dreamPopulator) {
        this.dreamPopulator = dreamPopulator;
    }

    private TripPopulator getTripPopulator() {
        return tripPopulator;
    }

    @Autowired
    public void setTripPopulator(TripPopulator tripPopulator) {
        this.tripPopulator = tripPopulator;
    }

    private InterestPopulator getInterestPopulator() {
        return interestPopulator;
    }

    @Autowired
    public void setInterestPopulator(InterestPopulator interestPopulator) {
        this.interestPopulator = interestPopulator;
    }

    private ChildrenPopulator getChildrenPopulator() {
        return childrenPopulator;
    }

    @Autowired
    public void setChildrenPopulator(ChildrenPopulator childrenPopulator) {
        this.childrenPopulator = childrenPopulator;
    }

    private JobPopulator getJobPopulator() {
        return jobPopulator;
    }

    @Autowired
    public void setJobPopulator(JobPopulator jobPopulator) {
        this.jobPopulator = jobPopulator;
    }

    private EducationPopulator getEducationPopulator() {
        return educationPopulator;
    }

    @Autowired
    public void setEducationPopulator(EducationPopulator educationPopulator) {
        this.educationPopulator = educationPopulator;
    }

    @Override
    public ProfileData populate(final AccountItem accountItem) {
        if (accountItem == null) {
            return null;
        }
        final AccountPhotoItem facePhoto = getPhotoService().getByAccount(accountItem.getId());

        final ProfileData profileData = new ProfileData();
        profileData.setId(accountItem.getId());
        profileData.setFirstName(accountItem.getFirstName());
        profileData.setLastName(accountItem.getLastName());

        profileData.setOnline(accountItem.isOnline());

        /* Educations populate*/
        if (!accountItem.getEducationItems().isEmpty()) {
            profileData.setEducation(getEducationPopulator().populate(accountItem.getEducationItems().iterator().next()));
        }

        /* Jobs populate*/
        if (!accountItem.getJobItems().isEmpty()) {
            profileData.setJob(getJobPopulator().populate(accountItem.getJobItems().iterator().next()));
        }

        /* Children populate*/
        if (!accountItem.getChildrenItems().isEmpty()) {
            profileData.setChildren(getChildrenPopulator().populate(accountItem.getChildrenItems().iterator().next()));
        }

        /* Interests populate*/
        final List<InterestData> interests = new ArrayList<>();
        for (InterestItem item : accountItem.getInterestItems()) {
            interests.add(getInterestPopulator().populate(item));
        }
        profileData.setInterests(interests);

        /* Dreams populate*/
        final List<DreamData> dreams = new ArrayList<>();
        for (DreamItem item : accountItem.getDreamItems()) {
            dreams.add(getDreamPopulator().populate(item));
        }
        profileData.setDreams(dreams);

        /* Trips populate*/
        final List<TripData> trips = new ArrayList<>();
        for (TripItem item : accountItem.getTripItems()) {
            trips.add(getTripPopulator().populate(item));
        }
        profileData.setTrips(trips);

        if (facePhoto != null) {
            profileData.setFacePhotoUrl(facePhoto.getName());
            File f = new File(facePhoto.getName());
            profileData.setFacePhotoUrl(f.getParent() + "/" + "thumb_" + f.getName());
            profileData.setPhoto60x60(f.getParent() + "/" + "60x60_" + f.getName());
        }

        final List<AccountGiftItem> gifts = getAccountGiftDao().getGiftsByAccount(profileData.getId());
        final List<AccountGiftData> giftData = new ArrayList<>();
        for (final AccountGiftItem accountGiftItem : gifts) {
            giftData.add(getAccountGiftDataPopulator().populate(accountGiftItem));
        }
        profileData.setGifts(giftData);
        profileData.setBalance(getBalanceDao().createOrGet(accountItem).getSum());


        if (authorityContains(accountItem.getAuthorities(), "ROLE_CREATOR")) {
            profileData.setStatus("ROLE_CREATOR");
        } else if (authorityContains(accountItem.getAuthorities(), "ROLE_PARTNER")) {
            profileData.setStatus("ROLE_PARTNER");
        } else {
            profileData.setStatus("ROLE_USER");
        }

        final String showOnline = getSettingService().getAccountSetting(profileData.getId(), "showOnline");
        // По умолчанию значение показывать что пользовать онлайн.
        if (showOnline == null) {
            profileData.setShowOnline(true);
        } else {
            profileData.setShowOnline(Boolean.valueOf(showOnline));
        }

        // По умолчанию значение показывать статус пользователя.
        final String showPremium = getSettingService().getAccountSetting(profileData.getId(), "showPremium");
        if (showPremium == null) {
            profileData.setShowPremium(true);
        } else {
            profileData.setShowPremium(Boolean.valueOf(showPremium));
        }

        profileData.setTheme(getSettingService().getAccountSetting(profileData.getId(), "theme"));
        profileData.setReferCode(accountItem.getReferCode());
        profileData.setMaritalStatus(accountItem.getMaritalStatus());

        if (accountItem.getBirthdate() != null) {
            profileData.setBirthDate(FORMAT_DD_MM_yyyy.format(accountItem.getBirthdate().getTime()));
            profileData.setAge(calculateAge(accountItem.getBirthdate()));
            Calendar birthdate = accountItem.getBirthdate();
            profileData.setZodiac(zodiac(birthdate.get(Calendar.MONTH) + 1, birthdate.get(Calendar.DAY_OF_MONTH)));
        }

        List<VideoData> videos = new ArrayList<>();
        for (VideoItem videoItem : accountItem.getVideosItems()) {
            videos.add(getVideoPopulator().populate(videoItem));
        }
        profileData.setVideos(videos);

        return profileData;
    }

    private boolean authorityContains(Set<AuthorityItem> authorities, String role) {
        for (AuthorityItem authority : authorities)
            if (authority.getRole().equals(role)) return true;
        return false;
    }

    private AccountGiftDataPopulator getAccountGiftDataPopulator() {
        return accountGiftDataPopulator;
    }

    @Autowired
    public void setAccountGiftDataPopulator(AccountGiftDataPopulator accountGiftDataPopulator) {
        this.accountGiftDataPopulator = accountGiftDataPopulator;
    }

    public int zodiac(int month, int day) {
        if ((month == 12 && day >= 22 && day <= 31) || (month == 1 && day >= 1 && day <= 19))
            return 1;
        else if ((month == 1 && day >= 20 && day <= 31) || (month == 2 && day >= 1 && day <= 17))
            return 2;
        else if ((month == 2 && day >= 18 && day <= 29) || (month == 3 && day >= 1 && day <= 19))
            return 3;
        else if ((month == 3 && day >= 20 && day <= 31) || (month == 4 && day >= 1 && day <= 19))
            return 4;
        else if ((month == 4 && day >= 20 && day <= 30) || (month == 5 && day >= 1 && day <= 20))
            return 5;
        else if ((month == 5 && day >= 21 && day <= 31) || (month == 6 && day >= 1 && day <= 20))
            return 6;
        else if ((month == 6 && day >= 21 && day <= 30) || (month == 7 && day >= 1 && day <= 22))
            return 7;
        else if ((month == 7 && day >= 23 && day <= 31) || (month == 8 && day >= 1 && day <= 22))
            return 8;
        else if ((month == 8 && day >= 23 && day <= 31) || (month == 9 && day >= 1 && day <= 22))
            return 9;
        else if ((month == 9 && day >= 23 && day <= 30) || (month == 10 && day >= 1 && day <= 22))
            return 10;
        else if ((month == 10 && day >= 23 && day <= 31) || (month == 11 && day >= 1 && day <= 21))
            return 11;
        else if ((month == 11 && day >= 22 && day <= 30) || (month == 12 && day >= 1 && day <= 21))
            return 12;
        return 0;
    }
}
