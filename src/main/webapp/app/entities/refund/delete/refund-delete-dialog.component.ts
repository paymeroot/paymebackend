import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';

@Component({
  standalone: true,
  templateUrl: './refund-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RefundDeleteDialogComponent {
  refund?: IRefund;

  protected refundService = inject(RefundService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.refundService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
