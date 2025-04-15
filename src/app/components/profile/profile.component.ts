import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      Lastname: ['', Validators.required],
      headline: ['', Validators.required],
      EmailAdress: ['', Validators.required],
      Number: ['', Validators.required],
     
    });
  }

  ngOnInit() {
    // You can load user data here and patch the form
    this.profileForm.patchValue({
      name: 'John',
      surname: 'Doe',
      headline: 'UI/UX Developer',
      EmailAdress: 'UI/UX Developer at Boston',
      Number: 'Boston University',
     
    });
  }

  onSubmit() {
    if (this.profileForm.valid) {
      console.log(this.profileForm.value);
      // Add your save logic here
    }
  }
}
