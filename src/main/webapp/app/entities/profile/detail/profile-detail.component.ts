import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IProfile } from '../profile.model';

@Component({
  standalone: true,
  selector: 'jhi-profile-detail',
  templateUrl: './profile-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ProfileDetailComponent {
  profile = input<IProfile | null>(null);

  previousState(): void {
    window.history.back();
  }
}
