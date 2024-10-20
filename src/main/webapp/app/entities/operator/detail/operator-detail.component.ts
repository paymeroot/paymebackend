import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IOperator } from '../operator.model';

@Component({
  standalone: true,
  selector: 'jhi-operator-detail',
  templateUrl: './operator-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class OperatorDetailComponent {
  operator = input<IOperator | null>(null);

  previousState(): void {
    window.history.back();
  }
}
