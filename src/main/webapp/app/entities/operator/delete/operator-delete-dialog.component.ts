import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IOperator } from '../operator.model';
import { OperatorService } from '../service/operator.service';

@Component({
  standalone: true,
  templateUrl: './operator-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class OperatorDeleteDialogComponent {
  operator?: IOperator;

  protected operatorService = inject(OperatorService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.operatorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
