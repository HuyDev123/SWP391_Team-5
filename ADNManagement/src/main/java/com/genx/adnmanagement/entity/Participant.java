package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Participant")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "cccd_number", length = 50)
    private String cccdNumber;

    @Column(name = "cccd_issued_date")
    private LocalDate cccdIssuedDate;

    @Column(name = "cccd_issued_place", length = 255)
    private String cccdIssuedPlace;

    @Column(name = "relationship", length = 50)
    private String relationship;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<TestSample> testSamples;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCccdNumber() { return cccdNumber; }
    public void setCccdNumber(String cccdNumber) { this.cccdNumber = cccdNumber; }
    public LocalDate getCccdIssuedDate() { return cccdIssuedDate; }
    public void setCccdIssuedDate(LocalDate cccdIssuedDate) { this.cccdIssuedDate = cccdIssuedDate; }
    public String getCccdIssuedPlace() { return cccdIssuedPlace; }
    public void setCccdIssuedPlace(String cccdIssuedPlace) { this.cccdIssuedPlace = cccdIssuedPlace; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public List<TestSample> getTestSamples() { return testSamples; }
    public void setTestSamples(List<TestSample> testSamples) { this.testSamples = testSamples; }
} 