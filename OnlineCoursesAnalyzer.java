import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is just a demo for you, please run it on JDK17. This is just a demo, and you can extend and
 * implement functions based on this demo, or implement it in a different way.
 */

class Course {

    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
        String title, String instructors, String subject,
        int year, int honorCode, int participants,
        int audited, int certified, double percentAudited,
        double percentCertified, double percentCertified50,
        double percentVideo, double percentForum, double gradeHigherZero,
        double totalHours, double medianHoursCertification,
        double medianAge, double percentMale, double percentFemale,
        double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public int getParticipants() {
        return participants;
    }

    public double getTotalHours() {
        return totalHours;
    }

}

public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4],
                    info[5],
                    Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                    Integer.parseInt(info[9]), Integer.parseInt(info[10]),
                    Double.parseDouble(info[11]),
                    Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                    Double.parseDouble(info[14]),
                    Double.parseDouble(info[15]), Double.parseDouble(info[16]),
                    Double.parseDouble(info[17]),
                    Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                    Double.parseDouble(info[20]),
                    Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //1
    public Map<String, Integer> getPtcpCountByInst() {
        return courses.stream()
            .collect(Collectors.groupingBy(course -> course.institution,
                Collectors.summingInt(Course::getParticipants)))
            .entrySet().stream()
            .sorted(Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                LinkedHashMap::new));
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        return courses.stream()
            .collect(Collectors.groupingBy(
                course -> course.institution + "-" + course.subject,
                Collectors.summingInt(Course::getParticipants)))
            .entrySet().stream()
            .sorted(Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                LinkedHashMap::new));
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() throws IOException {
        Set<String> list = courses.stream()
            .map(course -> course.instructors)
            .flatMap(instructor -> Arrays.stream(instructor.split(",")))
            .map(String::trim)
            .collect(Collectors.toSet());
        Map<String, List<List<String>>> result = new HashMap<>();
        for (String instructor : list) {
            List<List<String>> courses = new ArrayList<>();
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();
            for (Course course : this.courses) {
                String[] tmp = course.instructors.split(",");
                if (tmp.length == 1 && course.instructors.equals(instructor)) {
                    list1.add(course.title);
                    continue;
                }
                for (String s : tmp) {
                    if (s.trim().equals(instructor)) {
                        list2.add(course.title);
                        break;
                    }
                }
            }
            list1 = list1.stream().distinct().sorted().toList();
            list2 = list2.stream().distinct().sorted().toList();
            courses.add(list1);
            courses.add(list2);
            result.put(instructor, courses);
        }
        return result;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        Comparator<Course> comparator = switch (by.toLowerCase()) {
            case "participants" -> Comparator.comparing(Course::getParticipants).reversed();
            case "hours" -> Comparator.comparing(Course::getTotalHours).reversed();
            default -> throw new IllegalArgumentException("Invalid sorting key");
        };
        return courses.stream()
            .sorted(comparator)
            .map(course -> course.title)
            .distinct().limit(topK).toList();
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited,
        double totalCourseHours) {
        String finalCourseSubject = courseSubject.toLowerCase();
        return courses.stream()
            .filter(course -> course.subject.toLowerCase().contains(finalCourseSubject))
            .filter(course -> course.percentAudited >= percentAudited)
            .filter(course -> course.totalHours <= totalCourseHours)
            .map(course -> course.title)
            .distinct().sorted().toList();
    }

    //6

    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        // get all the data of the courses
        Map<String, Double> medianAge = new HashMap<>();
        Map<String, Double> male = new HashMap<>();
        Map<String, Double> BDH = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        Map<String, Course> data = new HashMap<>();
        for (Course c : courses) {
            if (cnt.containsKey(c.number)) {
                if (c.launchDate.compareTo(data.get(c.number).launchDate) > 0) {
                    data.put(c.number, c);
                }
                cnt.put(c.number, cnt.get(c.number) + 1);
                medianAge.put(c.number, medianAge.get(c.number) + c.medianAge);
                male.put(c.number, male.get(c.number) + c.percentMale);
                BDH.put(c.number, BDH.get(c.number) + c.percentDegree);
            } else {
                data.put(c.number, c);
                cnt.put(c.number, 1);
                medianAge.put(c.number, c.medianAge);
                male.put(c.number, c.percentMale);
                BDH.put(c.number, c.percentDegree);
            }
        }
        for (Map.Entry<String, Integer> entry : cnt.entrySet()) {
            medianAge.put(entry.getKey(), medianAge.get(entry.getKey()) / entry.getValue());
            male.put(entry.getKey(), male.get(entry.getKey()) / entry.getValue());
            BDH.put(entry.getKey(), BDH.get(entry.getKey()) / entry.getValue());
        }

        //get the result in all courses
        Map<String, Double> vals = new HashMap<>();
        for (Map.Entry<String, Integer> entry : cnt.entrySet()) {
            double val = Math.pow((age - medianAge.get(entry.getKey())), 2) + Math.pow(
                (gender * 100 - male.get(entry.getKey())), 2) + Math.pow(
                (isBachelorOrHigher * 100 - BDH.get(entry.getKey())), 2);
            vals.put(data.get(entry.getKey()).title, val);
        }

        //sort the result
        return vals.entrySet().stream()
            .sorted(Entry.<String, Double>comparingByValue().thenComparing(Entry.comparingByKey()))
            .map(Map.Entry::getKey)
            .distinct().limit(10).toList();
    }
}
