package com.canela.service.creditcardmgmt.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Client")
public class Client{
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name="id", nullable = false, unique = true)
    private Long id;
    
    @Column(name="document", nullable = false, unique = true)
    private Long document;
    
    @Column(name="type", nullable = false, unique = false)
    private String type;
    
    @Column(name="firstname", nullable = true, unique = false)
    private String firstname;
    
    @Column(name="lastname", nullable = true, unique = false)
    private String lastname;
    
    @Column(name="points", nullable = true, unique = false)
    private Long points;
    
    @Column(name="mail", nullable = true, unique = true)
    private String mail;
    
    @Column(name="phone", nullable = true, unique = true)
    private String phone;
    


	public Client() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocument() {
		return document;
	}

	public void setDocument(Long document) {
		this.document = document;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}



    
}
