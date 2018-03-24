package ru.todo100.activer.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.todo100.activer.dao.AccountDao;
import ru.todo100.activer.dao.CountryDao;
import ru.todo100.activer.dao.PhotoDao;
import ru.todo100.activer.data.InterestData;
import ru.todo100.activer.data.PhotoAvatarSizeData;
import ru.todo100.activer.data.ProfileData;
import ru.todo100.activer.data.TripData;
import ru.todo100.activer.form.*;
import ru.todo100.activer.model.*;
import ru.todo100.activer.populators.InterestPopulator;
import ru.todo100.activer.populators.TripPopulator;
import ru.todo100.activer.service.NewsService;
import ru.todo100.activer.service.PhotoService;
import ru.todo100.activer.util.ResizeImage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/settings")
public class SettingPageController {
    private CountryDao countryDao;
    private List<CountryItem> countryItems;
    @Autowired
    private PhotoService photoService1;
    @Autowired
    private AccountDao accountService;

    private InterestPopulator interestPopulator;
    private TripPopulator tripPopulator;
    @Autowired
    private NewsService newsService;
    private PhotoDao photoDao;
    @Value(value = "${static.host.upload}")
    private String STATIC_HOST_UPLOAD;

    private CountryDao getCountryDao() {
        return countryDao;
    }

    @Autowired
    public void setCountryDao(CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @ModelAttribute("countries")
    public List<CountryItem> getCountries() {
        if (countryItems == null) {
            countryItems = getCountryDao().getAll();
        }
        return countryItems;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String main(final Model model) {
        model.addAttribute("pageType", "settings/info");

        final AccountItem account = accountService.getCurrentAccount();

        model.addAttribute("photo", photoService1.getSizedPhoto(account.getId()));

        if (!model.containsAttribute("mainInfoForm")) {
            final PayPalAccountForm payPalAccountForm = new PayPalAccountForm();
//            mainInfoForm.setFirstName(account.getFirstName());
//            mainInfoForm.setLastName(account.getLastName());
//            mainInfoForm.setSex(account.getSex());
//            mainInfoForm.setMaritalStatus(account.getMaritalStatus());
//            mainInfoForm.setBirthDate(account.getBirthdate());
//            if (account.getBirthdate() != null) {
//                mainInfoForm.setBirthDate(account.getBirthdate());
//            }

            model.addAttribute("payPalForm", payPalAccountForm);
        }

        if (!model.containsAttribute("mainInfoForm")) {
            final MainInfoForm mainInfoForm = new MainInfoForm();
            mainInfoForm.setFirstName(account.getFirstName());
            mainInfoForm.setLastName(account.getLastName());
            mainInfoForm.setSex(account.getSex());
            mainInfoForm.setMaritalStatus(account.getMaritalStatus());
            mainInfoForm.setBirthDate(account.getBirthdate());
            if (account.getBirthdate() != null) {
                mainInfoForm.setBirthDate(account.getBirthdate());
            }

            model.addAttribute("mainInfoForm", mainInfoForm);
        }
        if (!model.containsAttribute("childrenEducationJobForm")) {
            final ChildrenEducationJobForm childrenEducationJobForm = new ChildrenEducationJobForm();
            final JobForm jobForm = new JobForm();
            childrenEducationJobForm.setJobForm(jobForm);
            final EducationForm educationForm = new EducationForm();
            childrenEducationJobForm.setEducationForm(educationForm);
            final ChildrenForm childrenForm = new ChildrenForm();
            childrenEducationJobForm.setChildrenForm(childrenForm);
            if (!account.getEducationItems().isEmpty()) {
                EducationItem education = account.getEducationItems().iterator().next();
                educationForm.setCity(education.getCity());
                educationForm.setUniversity(education.getUniversity());
                educationForm.setFaculty(education.getFaculty());
                if (education.getCountry() != null) {
                    educationForm.setCountry(education.getCountry().getCode());
                }
                educationForm.setYear(education.getEndYear());
            }

            if (!account.getJobItems().isEmpty()) {
                JobItem job = account.getJobItems().iterator().next();
                jobForm.setCity(job.getCity());
                jobForm.setPost(job.getPost());
                jobForm.setWork(job.getWorkplace());
            }

            if (!account.getChildrenItems().isEmpty()) {
                ChildrenItem child = account.getChildrenItems().iterator().next();
                childrenForm.setName(child.getChildrenName());
                childrenForm.setYear(child.getBirthdateYear());
            }

            model.addAttribute("childrenEducationJobForm", childrenEducationJobForm);
        }


        return "settings/index";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String mainPost(@Valid final MainInfoForm mainInfoForm, final BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            final AccountItem account = accountService.getCurrentAccount();
            account.setFirstName(mainInfoForm.getFirstName());
            account.setLastName(mainInfoForm.getLastName());
            account.setSex(mainInfoForm.getSex());
            account.setMaritalStatus(mainInfoForm.getMaritalStatus());
            account.setBirthdate(mainInfoForm.getBirthDate());
            accountService.save(account);

            accountService.addSynchronizer(account.getId(), "firstName", account.getFirstName());
            accountService.addSynchronizer(account.getId(), "lastName", account.getLastName());
            accountService.addSynchronizer(account.getId(), "sex", account.getSex());
            accountService.addSynchronizer(account.getId(), "maritalStatus", account.getMaritalStatus());
            accountService.addSynchronizer(account.getId(), "birthdate", account.getBirthdate());
        }
        return "redirect:/settings";
    }

    @RequestMapping(value = "/advancedPost", method = RequestMethod.POST)
    public String advancedPost(@Valid final ChildrenEducationJobForm childrenEducationJobForm, final BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            final AccountItem account = accountService.getCurrentAccount();
            final Set<EducationItem> educations = account.getEducationItems();

            final EducationItem education;
            if (educations.isEmpty()) {
                education = new EducationItem();
                educations.add(education);
            } else {
                education = educations.iterator().next();
            }

            education.setCity(childrenEducationJobForm.getEducationForm().getCity());
            education.setEndYear(childrenEducationJobForm.getEducationForm().getYear());
            education.setFaculty(childrenEducationJobForm.getEducationForm().getFaculty());
            education.setUniversity(childrenEducationJobForm.getEducationForm().getUniversity());

            for (CountryItem country : getCountries()) {
                if (country.getCode().equals(childrenEducationJobForm.getEducationForm().getCountry())) {
                    education.setCountry(country);
                }
            }


            Set<JobItem> jobs = account.getJobItems();


            final JobItem job;
            if (jobs.isEmpty()) {
                job = new JobItem();
                jobs.add(job);
            } else {
                job = jobs.iterator().next();
            }

            job.setCity(childrenEducationJobForm.getJobForm().getCity());
            job.setPost(childrenEducationJobForm.getJobForm().getPost());
            job.setWorkplace(childrenEducationJobForm.getJobForm().getWork());


            Set<ChildrenItem> children = account.getChildrenItems();

            final ChildrenItem child;
            if (children.size() == 0) {
                child = new ChildrenItem();
                children.add(child);
            } else {
                child = children.iterator().next();
            }

            child.setChildrenName(childrenEducationJobForm.getChildrenForm().getName());
            child.setBirthdateYear(childrenEducationJobForm.getChildrenForm().getYear());

            accountService.save(account);

            accountService.addSynchronizer(account.getId(), "education", account.getEducationItems());
            accountService.addSynchronizer(account.getId(), "job", account.getJobItems());
            accountService.addSynchronizer(account.getId(), "children", account.getChildrenItems());
        }
        return "redirect:/settings";
    }

    @ResponseBody
    @RequestMapping(value = "/uploadphoto", method = RequestMethod.POST)
    public PhotoAvatarSizeData uploadPhoto(@RequestParam(value = "photo", required = false) final MultipartFile photo) throws IOException {
        final String contentType = photo.getContentType();

        final File originalFile = new File(photo.getOriginalFilename());
        FileUtils.writeByteArrayToFile(originalFile, photo.getBytes());
        final String theString = sendFile(originalFile, contentType);


        final PhotoAvatarSizeData photoAvatarSizeData = new PhotoAvatarSizeData();
        photoAvatarSizeData.setPhotoOriginal(theString);

        final File fileShowingSize = getNewFile(originalFile, 1100, 700);
        String showingSize = sendFile(fileShowingSize, contentType);


        photoAvatarSizeData.setPhotoShowing(showingSize);


        final File fileThumbnailSize = getNewFile(originalFile, 200, 200);
        String size100 = sendFile(fileThumbnailSize, contentType);


        photoAvatarSizeData.setPhotoThumbnail(size100);


        fileShowingSize.deleteOnExit();
        fileThumbnailSize.deleteOnExit();
        originalFile.deleteOnExit();

        photoService1.setPhoto(accountService.getCurrentAccount().getId(), photoAvatarSizeData);

        AvatarNewsItem avatarNewsItem = new AvatarNewsItem();
        avatarNewsItem.setText(showingSize + ";" + size100);
        avatarNewsItem.setDate(new GregorianCalendar());
        avatarNewsItem.setAccountId(accountService.getCurrentAccount().getId());

        newsService.addNews(avatarNewsItem);

        return photoAvatarSizeData;
    }

    private String sendFile(File file, String contentType) throws IOException {
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        final HttpPost httppost = new HttpPost(STATIC_HOST_UPLOAD + "/static/upload");
        builder.addPart("image", new FileBody(file, ContentType.create(contentType)));
        httppost.setEntity(builder.build());
        final HttpResponse response = httpclient.execute(httppost);
        final StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
        return writer.toString();
    }

    private File getNewFile(File file, int width, int height) {
        String newName = RandomStringUtils.randomAlphanumeric(6);
        final File newFile = new File(newName);
        String extension = FilenameUtils.getExtension(file.getName());
        ResizeImage.resize(file, newFile, extension, width, height);
        return newFile;
    }

    private InterestPopulator getInterestPopulator() {
        return interestPopulator;
    }

    @Autowired
    public void setInterestPopulator(InterestPopulator interestPopulator) {
        this.interestPopulator = interestPopulator;
    }

    @RequestMapping(value = "/interests", method = RequestMethod.GET)
    public String interests(Model model) {
        model.addAttribute("pageType", "settings/info/interest");

        final AccountItem account = accountService.getCurrentAccount();

        Set<InterestItem> interestsItems = account.getInterestItems();
        List<InterestData> interests = new ArrayList<>();

        for (InterestItem item : interestsItems) {
            interests.add(getInterestPopulator().populate(item));
        }

        model.addAttribute("interests", interests);
        return "settings/interests";
    }

    @RequestMapping(value = "/interests", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public void interestsPost(InterestForm interestForm) {
        Set<InterestItem> interests = new HashSet<>();
        if (interestForm.getTags() != null) {
            for (String interest : interestForm.getTags()) {
                InterestItem interestItem = new InterestItem();
                interestItem.setName(interest);
                interests.add(interestItem);
            }

        }
        final AccountItem account = accountService.getCurrentAccount();

        account.setInterestItems(interests);
        accountService.save(account);
        accountService.deleteOldInterests();
        accountService.addSynchronizer(account.getId(), "interests", interests);

    }

    private TripPopulator getTripPopulator() {
        return tripPopulator;
    }

    @Autowired
    public void setTripPopulator(TripPopulator tripPopulator) {
        this.tripPopulator = tripPopulator;
    }

    @RequestMapping(value = "/trips", method = RequestMethod.GET)
    public String trips(final Model model) {
        model.addAttribute("pageType", "settings/info/trip");
        if (!model.containsAttribute("tripForm")) {
            model.addAttribute("tripForm", new TripForm());
        }
        final List<TripData> trips = new ArrayList<>();
        final AccountItem account = accountService.getCurrentAccount();
        for (final TripItem trip : account.getTripItems()) {
            trips.add(getTripPopulator().populate(trip));
        }
        model.addAttribute("trips", trips);
        return "settings/trips";
    }

    @RequestMapping(value = "/trips", method = RequestMethod.POST)
    public String tripsPost(@Valid final TripForm tripForm, final BindingResult bindingResult, final RedirectAttributes attr) {
        if (!bindingResult.hasErrors()) {
            final AccountItem account = accountService.getCurrentAccount();
            final TripItem tripItem = new TripItem();
            tripItem.setCity(tripForm.getCity());
            String[] date = tripForm.getYear().split("/");
            tripItem.setYear(Integer.parseInt(date[1]));
            for (final CountryItem country : getCountries()) {
                if (country.getCode().equals(tripForm.getCountry())) {
                    tripItem.setCountry(country);
                }
            }
            final Set<TripItem> trips = account.getTripItems();
            trips.add(tripItem);
            accountService.addSynchronizer(account.getId(), "trips", trips);
            accountService.save(account);
        } else {
            attr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "tripForm", bindingResult);
            attr.addFlashAttribute("tripForm", tripForm);
        }
        return "redirect:/settings/trips";
    }

    @RequestMapping(value = "/trips/remove", method = RequestMethod.GET)
    public String tripsPost(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("trip"));
            accountService.deleteTrip(id);
        } catch (NumberFormatException ignored) {
        }
        return "redirect:/settings/trips";
    }

    @RequestMapping("/dreams")
    public String dreams(final Model model, final HttpSession session) {
        model.addAttribute("pageType", "settings/info/dream");
        final DreamForm dreamForm = new DreamForm();
        model.addAttribute("dreamForm", dreamForm);
        final ProfileData profileData = accountService.getCurrentProfileData(session);
        model.addAttribute("dreams", profileData.getDreams());
        return "settings/dreams";
    }

    private void deletePhoto(String filename) throws IOException {
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpPost httppost = new HttpPost(STATIC_HOST_UPLOAD + "/static/remove/file?filename=" + URLEncoder.encode(filename, "UTF-8"));
        httpclient.execute(httppost);
    }

    @RequestMapping(value = "/dreams/upload", method = RequestMethod.POST)
    public String dreamsUpload(@Valid final DreamForm dreamForm, BindingResult bindingResult) throws IOException {
        if (!bindingResult.hasErrors()) {
            final AccountItem account = accountService.getCurrentAccount();

            DreamItem dreamItem = null;
            if (dreamForm.getId() != null) {
                for (DreamItem item : account.getDreamItems()) {
                    if (item.getId().equals(dreamForm.getId())) {
                        dreamItem = item;

                        //delete photo
                        if (!StringUtils.isEmpty(dreamItem.getPhoto()) &&
                                (dreamForm.getRemovePhoto() || (dreamForm.getPhoto() != null && !dreamForm.getPhoto().isEmpty()))) {
                            deletePhoto(dreamItem.getPhoto());
                            dreamItem.setPhoto(null);
                        }
                    }
                }
            }

            if (dreamItem == null) {
                dreamItem = new DreamItem();
            }

            dreamItem.setName(dreamForm.getText());

            if (dreamForm.getPhoto() != null && !dreamForm.getPhoto().isEmpty()) {
                final HttpClient httpclient = HttpClientBuilder.create().build();
                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                final File file = new File(dreamForm.getPhoto().getName());
                FileUtils.writeByteArrayToFile(file, dreamForm.getPhoto().getBytes());
                final HttpPost httppost = new HttpPost(STATIC_HOST_UPLOAD + "/static/upload");
                builder.addPart("image", new FileBody(file, ContentType.create(dreamForm.getPhoto().getContentType())));
                httppost.setEntity(builder.build());
                final HttpResponse response = httpclient.execute(httppost);
                final StringWriter writer = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
                final String theString = writer.toString();
                dreamItem.setPhoto(theString);
            }
            final Set<DreamItem> dreams = account.getDreamItems();
            dreams.add(dreamItem);
            accountService.addSynchronizer(account.getId(), "dreams", dreams);
            accountService.save(account);
        }
        return "redirect:/settings/dreams";
    }

    @RequestMapping(value = "/dreams/remove")
    public String removeDream(final HttpServletRequest request) throws IOException {
        try {
            final Integer id = Integer.parseInt(request.getParameter("dream"));

            final AccountItem account = accountService.getCurrentAccount();
            for (DreamItem dream : account.getDreamItems()) {
                if (Objects.equals(dream.getId(), id)) {
                    if (!StringUtils.isEmpty(dream.getPhoto())) {
                        deletePhoto(dream.getPhoto());
                    }
                    break;
                }
            }

            accountService.deleteDream(id);
            accountService.addSynchronizer(accountService.getCurrentAccount().getId(), "dreams", accountService.getCurrentAccount().getDreamItems());
        } catch (NumberFormatException ignored) {
        }
        return "redirect:/settings/dreams";
    }

    @RequestMapping(value = "/leftmenusave")
    @ResponseBody
    public void leftmenusave(final HttpServletRequest request) {
        if (request.getParameter("yes") != null) {
            request.getSession().setAttribute("leftmenu", 1);
        } else {
            request.getSession().removeAttribute("leftmenu");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/croppedSaveImage", method = RequestMethod.POST)
    public void croppedSaveImage(final HttpServletRequest request, @RequestParam(value = "croppedImage", required = false) final MultipartFile cropped) throws IOException {
        File file = null;
        File miniCroppedFile = null;
        try {
            // Always PNG came
            final String extension = ".png";
            final ProfileData profileData = accountService.getCurrentProfileData(request.getSession());
            final AccountPhotoItem accountPhotoItem = getPhotoDao().getByAccount(profileData.getId());
            file = new File(cropped.getOriginalFilename() + extension);
            FileUtils.writeByteArrayToFile(file, cropped.getBytes());
            final String croppedFileName = sendFile(file, cropped.getContentType());
            accountPhotoItem.setNameAvatar(croppedFileName);
            miniCroppedFile = getNewFile(file, 60, 60);
            final String miniCroppedFileName = sendFile(miniCroppedFile, cropped.getContentType());
            accountPhotoItem.setNameMini(miniCroppedFileName);
            getPhotoDao().save(accountPhotoItem);
        } finally {
            if (miniCroppedFile != null) {
                miniCroppedFile.deleteOnExit();
            }
            if (file != null) {
                file.deleteOnExit();
            }
        }
    }

    private PhotoDao getPhotoDao() {
        return photoDao;
    }

    @Autowired
    public void setPhotoDao(PhotoDao photoDao) {
        this.photoDao = photoDao;
    }
}
