import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IKyc } from '../kyc.model';

@Component({
  standalone: true,
  selector: 'jhi-kyc-detail',
  templateUrl: './kyc-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class KycDetailComponent {
  kyc = input<IKyc | null>(null);

  previousState(): void {
    window.history.back();
  }
}
