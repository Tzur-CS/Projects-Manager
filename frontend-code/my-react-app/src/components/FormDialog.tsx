import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
} from "@mui/material";

type FormField = {
  name: string;
  label: string;
  multiline?: boolean;
  rows?: number;
  autoFocus?: boolean;
};

type FormDialogProps = {
  open: boolean;
  title: string;
  onClose: () => void;
  onSubmit: () => void;
  fields: FormField[];
  formData: Record<string, string>;
  onFieldChange: (name: string, value: string) => void;
  isPending: boolean;
  submitLabel?: string;
};

const FormDialog = ({
  open,
  title,
  onClose,
  onSubmit,
  fields,
  formData,
  onFieldChange,
  isPending,
  submitLabel = "Add",
}: FormDialogProps) => {
  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        {fields.map((field) => (
          <TextField
            key={field.name}
            autoFocus={field.autoFocus}
            margin="dense"
            label={field.label}
            fullWidth
            multiline={field.multiline}
            rows={field.rows}
            value={formData[field.name] || ""}
            onChange={(e) => onFieldChange(field.name, e.target.value)}
            disabled={isPending}
          />
        ))}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isPending}>
          Cancel
        </Button>
        <Button onClick={onSubmit} variant="contained" disabled={isPending}>
          {isPending ? "Saving..." : submitLabel}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FormDialog;
